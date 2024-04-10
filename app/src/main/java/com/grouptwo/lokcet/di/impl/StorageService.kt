package com.grouptwo.lokcet.di.impl

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.grouptwo.lokcet.data.model.UploadImage
import com.grouptwo.lokcet.di.service.StorageService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StorageServiceImpl @Inject constructor(
    private val auth: FirebaseAuth, private val firestore: FirebaseFirestore
) : StorageService {
    @OptIn(ExperimentalCoroutinesApi::class)
    override val images: Flow<List<UploadImage>> get() = emptyFlow()
    override suspend fun uploadImage(image: UploadImage) {}

    override suspend fun getImages(): List<UploadImage> {
//        return emptyList()
        // Get friends list from current user

        val friends = firestore.collection("users").document(auth.currentUser?.uid.orEmpty()).get().await().get("friends") as List<String>
        // Get images from friends and sort by createdAt field to descending order
        return firestore.collection("images")
            .whereIn("userId", friends)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()
            .toObjects(UploadImage::class.java)
    }


    override suspend fun deleteImage(imageId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getImagesUploadByUser(userId: String): List<UploadImage> {
        TODO("Not yet implemented")
    }

}