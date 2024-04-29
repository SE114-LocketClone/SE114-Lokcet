package com.grouptwo.lokcet.di.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.grouptwo.lokcet.data.model.EmojiReaction
import com.grouptwo.lokcet.data.model.Feed
import com.grouptwo.lokcet.data.model.UploadImage
import com.grouptwo.lokcet.utils.Constants
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FeedPagingSource(
    private val firestore: FirebaseFirestore, private val friendIds: List<String>
) : PagingSource<QuerySnapshot, Feed>() {
    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Feed> {
        return try {
            val currentPage = params.key ?: runBlocking {
                friendIds.chunked(10).flatMap { chunk ->
                    chunk.map { friendId ->
                        async {
                            firestore.collection("images")
                                .whereEqualTo("userId", friendId)
                                .orderBy("createdAt", Query.Direction.DESCENDING)
                                .limit(Constants.PAGE_SIZE)
                                .get()
                                .await()
                        }
                    }.awaitAll()
                }.flatten().sortedByDescending { it.getDate("createdAt") }
                    .take(Constants.PAGE_SIZE.toInt())
            }

            // Get the first and last document snapshot of the current page to determine the next and previous page
            val firstDocumentSnapshot = currentPage.first()
            val lastDocumentSnapshot = currentPage.last()

            val nextPage = firestore.collection("images")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .startAfter(lastDocumentSnapshot)
                .limit(Constants.PAGE_SIZE)
                .get()
                .await()

            val prevPage = if (params.key != null) {
                firestore.collection("images")
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .endBefore(firstDocumentSnapshot)
                    .limit(Constants.PAGE_SIZE)
                    .get()
                    .await()
            } else null

            LoadResult.Page(
                data = currentPage.map { documentSnapshot ->
                    val uploadImage = documentSnapshot.toObject(UploadImage::class.java)
                    val emojiReactions = firestore.collection("reaction")
                        .whereEqualTo("imageId", uploadImage.imageId)
                        .get()
                        .await()
                        .toObjects(EmojiReaction::class.java)

                    Feed(uploadImage, emojiReactions)
                },
                prevKey = if (prevPage == null || prevPage.isEmpty) null else prevPage,
                nextKey = if (nextPage == null || nextPage.isEmpty) null else nextPage
            )

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<QuerySnapshot, Feed>): QuerySnapshot? {
        //  Return the anchor position of the closest page to the anchor position
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
        }
    }
}

