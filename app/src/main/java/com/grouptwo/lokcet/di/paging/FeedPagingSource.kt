package com.grouptwo.lokcet.di.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.grouptwo.lokcet.data.model.EmojiReaction
import com.grouptwo.lokcet.data.model.Feed
import com.grouptwo.lokcet.data.model.UploadImage
import com.grouptwo.lokcet.di.service.AccountService
import com.grouptwo.lokcet.utils.Constants
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

//class FeedPagingSource(
//    private val firestore: FirebaseFirestore, private val friendIds: List<String>
//) : PagingSource<QuerySnapshot, Feed>() {
//    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Feed> {
//        return try {
//            val currentPage = params.key ?: coroutineScope {
//                friendIds.map { friendId ->
//                    async {
//                        firestore.collection("images").whereEqualTo("userId", friendId)
//                            .orderBy("createdAt", Query.Direction.DESCENDING)
//                            .limit(Constants.PAGE_SIZE).get().await()
//                    }
//                }.awaitAll()
//            }.flatten().sortedByDescending { it.getDate("createdAt") }
//                .take(Constants.PAGE_SIZE.toInt())
//
//            val currentList = currentPage.toList()
//            if (currentList.isEmpty()) {
//                return LoadResult.Page(
//                    data = emptyList(), prevKey = null, nextKey = null
//                )
//            }
//            // Get the first and last document snapshot of the current page to determine the next and previous page
//            val firstDocumentSnapshot = currentList.first()
//            val lastDocumentSnapshot = currentList.last()
//            // Check if only specific friendIds 1 id is passed
//            // If one then nextPage and prevPage will have .whereEqualTo("userId", friendId)
//            if (friendIds.size == 1) {
//                val nextPage = firestore.collection("images").whereEqualTo("userId", friendIds[0])
//                    .orderBy("createdAt", Query.Direction.DESCENDING)
//                    .startAfter(lastDocumentSnapshot).limit(Constants.PAGE_SIZE).get().await()
//
//                val prevPage = if (params.key != null) {
//                    firestore.collection("images").whereEqualTo("userId", friendIds[0])
//                        .orderBy("createdAt", Query.Direction.DESCENDING)
//                        .endBefore(firstDocumentSnapshot).limit(Constants.PAGE_SIZE).get().await()
//                } else null
//                return LoadResult.Page(
//                    data = currentPage.map { documentSnapshot ->
//                        val uploadImage = documentSnapshot.toObject(UploadImage::class.java)
//                        val emojiReactions = firestore.collection("reaction")
//                            .whereEqualTo("imageId", uploadImage.imageId).get().await()
//                            .toObjects(EmojiReaction::class.java)
//
//                        Feed(uploadImage, emojiReactions)
//                    },
//                    prevKey = if (prevPage == null || prevPage.isEmpty) null else prevPage,
//                    nextKey = if (nextPage == null || nextPage.isEmpty) null else nextPage
//                )
//            }
//            // Convert the first and last document snapshot to query snapshot
//            val nextPage =
//                firestore.collection("images").orderBy("createdAt", Query.Direction.DESCENDING)
//                    .startAfter(lastDocumentSnapshot).limit(Constants.PAGE_SIZE).get().await()
//
//            val prevPage = if (params.key != null) {
//                firestore.collection("images").orderBy("createdAt", Query.Direction.DESCENDING)
//                    .endBefore(firstDocumentSnapshot).limit(Constants.PAGE_SIZE).get().await()
//            } else null
//
//            LoadResult.Page(
//                data = currentPage.map { documentSnapshot ->
//                    val uploadImage = documentSnapshot.toObject(UploadImage::class.java)
//                    val emojiReactions = firestore.collection("reaction")
//                        .whereEqualTo("imageId", uploadImage.imageId).get().await()
//                        .toObjects(EmojiReaction::class.java)
//
//                    Feed(uploadImage, emojiReactions)
//                },
//                prevKey = if (prevPage == null || prevPage.isEmpty) null else prevPage,
//                nextKey = if (nextPage == null || nextPage.isEmpty) null else nextPage
//            )
//
//        } catch (e: Exception) {
//            LoadResult.Error(e)
//        }
//    }
//
//    override fun getRefreshKey(state: PagingState<QuerySnapshot, Feed>): QuerySnapshot? {
//        //  Return the anchor position of the closest page to the anchor position
//        return state.anchorPosition?.let { anchorPosition ->
//            state.closestPageToPosition(anchorPosition)?.prevKey
//        }
//    }
//}

//            val currentPage = coroutineScope {
//                params.key?.let { key ->
//                    friendIds.map { friendId ->
//                        async {
//                            firestore.collection("images").whereEqualTo("userId", friendId)
//                                .orderBy("createdAt", Query.Direction.DESCENDING)
//                                .startAfter(key).limit(Constants.PAGE_SIZE).get().await().documents
//                        }
//                    }.awaitAll().flatten().sortedByDescending { it.getDate("createdAt") }
//                        .take(Constants.PAGE_SIZE.toInt())
//                } ?: friendIds.map { friendId ->
//                    async {
//                        firestore.collection("images").whereEqualTo("userId", friendId)
//                            .orderBy("createdAt", Query.Direction.DESCENDING)
//                            .limit(Constants.PAGE_SIZE).get().await().documents
//                    }
//                }.awaitAll().flatten().sortedByDescending { it.getDate("createdAt") }
//                    .take(Constants.PAGE_SIZE.toInt())
//            }
class FeedPagingSource(
    private val firestore: FirebaseFirestore,
    private val friendIds: List<String>,
    private val accountService: AccountService
) : PagingSource<DocumentSnapshot, Feed>() {

    override suspend fun load(params: LoadParams<DocumentSnapshot>): LoadResult<DocumentSnapshot, Feed> {
        return try {
            val currentUserId = accountService.currentUserId
            val currentPage = coroutineScope {
                params.key?.let { key ->
                    // Querry for images that are visible to all users
                    val visibleToAllImages = friendIds.map { friendId ->
                        async {
                            firestore.collection("images").whereEqualTo("userId", friendId)
                                .whereEqualTo("isVisibleToAll", true)
                                .orderBy("createdAt", Query.Direction.DESCENDING)
                                .startAfter(key).limit(Constants.PAGE_SIZE).get().await().documents
                        }
                    }.awaitAll().flatten().sortedByDescending { it.getDate("createdAt") }
                    // Querry for images that are visible to the current user
                    val visibleToUserImages = friendIds.map { friendId ->
                        async {
                            firestore.collection("images").whereEqualTo("userId", friendId)
                                .whereArrayContains("visibleUserIds", currentUserId)
                                .orderBy("createdAt", Query.Direction.DESCENDING)
                                .startAfter(key).limit(Constants.PAGE_SIZE).get().await().documents
                        }
                    }.awaitAll().flatten().sortedByDescending { it.getDate("createdAt") }

                    (visibleToAllImages + visibleToUserImages).sortedByDescending { it.getDate("createdAt") }
                        .take(Constants.PAGE_SIZE.toInt())
                } ?: let {
                    // Querry for images that are visible to all users
                    val visibleToAllImages = friendIds.map { friendId ->
                        async {
                            firestore.collection("images").whereEqualTo("userId", friendId)
                                .whereEqualTo("isVisibleToAll", true)
                                .orderBy("createdAt", Query.Direction.DESCENDING)
                                .limit(Constants.PAGE_SIZE).get().await().documents
                        }
                    }.awaitAll().flatten().sortedByDescending { it.getDate("createdAt") }
                    // Querry for images that are visible to the current user
                    val visibleToUserImages = friendIds.map { friendId ->
                        async {
                            firestore.collection("images").whereEqualTo("userId", friendId)
                                .whereArrayContains("visibleUserIds", currentUserId)
                                .orderBy("createdAt", Query.Direction.DESCENDING)
                                .limit(Constants.PAGE_SIZE).get().await().documents
                        }
                    }.awaitAll().flatten().sortedByDescending { it.getDate("createdAt") }

                    (visibleToAllImages + visibleToUserImages).sortedByDescending { it.getDate("createdAt") }
                        .take(Constants.PAGE_SIZE.toInt())
                }
            }

            val currentList = currentPage.toList()
            if (currentList.isEmpty()) {
                return LoadResult.Page(
                    data = emptyList(), prevKey = null, nextKey = null
                )
            }

            val data = currentPage.map { documentSnapshot ->
                val uploadImage = documentSnapshot.toObject(UploadImage::class.java)
                val emojiReactions = firestore.collection("reactions")
                    .whereEqualTo("imageId", uploadImage?.imageId)
                    .orderBy("createdAt", Query.Direction.DESCENDING).get().await()
                    .toObjects(EmojiReaction::class.java)
                Feed(uploadImage!!, emojiReactions)
            }

            LoadResult.Page(
                data = data,
                prevKey = null,  // This can be null if you don't need to load data in reverse.
                nextKey = currentPage.lastOrNull()
            )

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<DocumentSnapshot, Feed>): DocumentSnapshot? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
        }
    }
}