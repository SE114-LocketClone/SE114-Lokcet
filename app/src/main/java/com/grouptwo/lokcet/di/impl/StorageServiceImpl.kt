package com.grouptwo.lokcet.di.impl

import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.grouptwo.lokcet.data.model.UploadImage
import com.grouptwo.lokcet.di.service.AccountService
import com.grouptwo.lokcet.di.service.StorageService
import com.grouptwo.lokcet.utils.DataState
import com.grouptwo.lokcet.utils.getImageNameFromUrl
import com.grouptwo.lokcet.utils.toBitmap
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class StorageServiceImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val accountService: AccountService
) : StorageService {
    @OptIn(ExperimentalCoroutinesApi::class)
    override val images: Flow<List<UploadImage>> get() = emptyFlow()
    override suspend fun uploadImage(
        imageUpload: ByteArray,
        imageCaption: String,
        visibleUserIds: List<String> // if empty, image is visible to all users else only to the users in visibleUserIds list can see the image
    ): Flow<DataState<Unit>> = flow {
        emit(DataState.Loading)
        try {
            val storageService = storage.reference
            // Create image name
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val imageName = "IMG_${auth.currentUser?.uid}_$timestamp"
            // Create image reference
            val imageRef = storageService.child("images/$imageName")
            // Upload image to Firebase Storage
            imageRef.putBytes(imageUpload).await()
            // Get download URL
            val downloadUrl = imageRef.downloadUrl.await()
            // Get current user
            val user = accountService.getCurrentUser()
            // Create image object
            val image = UploadImage(
                userId = user.id,
                imageUrl = downloadUrl.toString(),
                imageCaption = imageCaption,
                visibleUserIds = visibleUserIds,
                isVisibleToAll = visibleUserIds.isEmpty(), // If visibleUserIds is null, then the image is visible to all users else only to the users in visibleUserIds list can see the image
                userName = "${user.firstName} ${user.lastName}",
            )
            // Save image to Firestore
            firestore.collection("images").add(image).await()
            // Update imageId in image object
            firestore.collection("images").whereEqualTo("imageUrl", downloadUrl.toString())
                .get().await().documents.firstOrNull()?.let {
                    firestore.collection("images").document(it.id)
                        .update("imageId", it.id).await()
                }
            // Add image to user's uploadImageList
            firestore.collection("users").document(auth.currentUser?.uid.orEmpty())
                .update("uploadImageList", FieldValue.arrayUnion(image.imageUrl)).await()
            emit(DataState.Success(Unit))
        } catch (e: Exception) {
            // Handle any other exceptions
            emit(DataState.Error(e))
        }
    }


    override suspend fun deleteImage(imageId: String): Flow<DataState<Unit>> {
        return flow {
            try {
                emit(DataState.Loading)
                // Get image from Firestore
                val image = firestore.collection("images").document(imageId).get().await()
                val imageName = image.getString("imageUrl")?.getImageNameFromUrl()

                coroutineScope {
                    val deleteFromFirestore = async { firestore.collection("images").document(imageId).delete().await() }
                    val deleteFromStorage = async { storage.reference.child("images/$imageName").delete().await() }
                    val removeFromUploadImageList = async {
                        firestore.collection("users").document(auth.currentUser?.uid.orEmpty())
                            .update("uploadImageList", FieldValue.arrayRemove(image.getString("imageUrl").orEmpty())).await()
                    }

                    val deleteReactions = async {
                        val reactionDocs = firestore.collection("reactions").whereEqualTo("imageId", imageId).get().await().documents
                        reactionDocs.forEach { firestore.collection("reactions").document(it.id).delete().await() }
                    }

                    val deleteChatroomMessages = async {
                        val chatRoomDocs = firestore.collection("chatrooms").get().await().documents
                        chatRoomDocs.forEach { chatRoomDoc ->
                            val messageDocs = firestore.collection("chatrooms").document(chatRoomDoc.id).collection("messages").get().await().documents
                            messageDocs.forEach { messageDoc ->
                                val messageMap = messageDoc.get("feed") as Map<*, *>
                                if (messageMap["imageId"] == imageId) {
                                    firestore.collection("chatrooms").document(chatRoomDoc.id).collection("messages").document(messageDoc.id).delete().await()
                                    // Delete in latest_messages
                                    firestore.collection("latest_messages").document(chatRoomDoc.id).delete().await()
                                }
                            }
                        }
                    }

                    val deleteLatestMessages = async {
                        val chatRoomDocs = firestore.collection("latest_messages").get().await().documents
                        chatRoomDocs.forEach { chatRoomDoc ->
                            val messageMap = chatRoomDoc.get("message") as Map<*, *>
                            if (messageMap["imageId"] == imageId) {
                                firestore.collection("latest_messages").document(chatRoomDoc.id).delete().await()
                            }
                        }
                    }

                    // Await all operations
                    deleteFromFirestore.await()
                    deleteFromStorage.await()
                    removeFromUploadImageList.await()
                    deleteReactions.await()
                    deleteChatroomMessages.await()
                    deleteLatestMessages.await()
                }
                emit(DataState.Success(Unit))
            } catch (e: Exception) {
                // Handle any other exceptions
                emit(DataState.Error(e))
            }
        }
    }

    override suspend fun getImagesUploadByUser(userId: String): List<UploadImage> {
        TODO("Not yet implemented")
    }

    override suspend fun downloadImage(imageUrl: String): Bitmap {
        try {
            // Download image from Firebase Storage
            val storage = storage.reference
            // get the image name from url
            val imageName = imageUrl.getImageNameFromUrl()
            // get the image from storage
            val imageRef = storage.child("images/$imageName")
            val image = imageRef.getBytes(2 * 1024 * 1024).await()
            if (image.isEmpty()) {
                throw Exception("Không thể tải ảnh")
            } else {
                return image.toBitmap()
            }
        } catch (e: Exception) {
            throw e
        }
    }

}