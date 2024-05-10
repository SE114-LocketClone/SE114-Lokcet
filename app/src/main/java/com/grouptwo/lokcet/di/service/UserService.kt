package com.grouptwo.lokcet.di.service

import com.grouptwo.lokcet.data.model.User
import com.grouptwo.lokcet.utils.DataState
import kotlinx.coroutines.flow.Flow

interface UserService {
    suspend fun getSuggestFriendList():Flow<DataState<List<User>>>
    suspend fun acceptFriend(userId: String, friendId: String): Flow<DataState<Unit>>
    suspend fun rejectFriend(userId: String, friendId: String): Flow<DataState<Unit>>
    suspend fun addFriend(userId: String, friendId: String): Flow<DataState<Unit>>
    suspend fun removeFriend(userId: String, friendId: String): Flow<DataState<Unit>>
    suspend fun getFriendList(): Flow<DataState<List<User>>>
    suspend fun getWaitedFriendList(): Flow<DataState<List<User>>>
    suspend fun getRequestFriendList(): Flow<DataState<List<User>>>
    suspend fun removeWaitedFriend(userId: String, friendId: String): Flow<DataState<Unit>>
    suspend fun addEmojiReaction(feedId: String, emoji: String): Flow<DataState<Unit>>

    suspend fun getNumOfNewFeeds(
        friendIds: List<String>,
    ): Flow<DataState<Int>>

}