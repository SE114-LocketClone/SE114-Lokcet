package com.grouptwo.lokcet.di.service

import com.grouptwo.lokcet.data.model.User
import com.grouptwo.lokcet.utils.DataState
import kotlinx.coroutines.flow.Flow

interface UserService {
    suspend fun getSuggestFriendList():Flow<DataState<List<User>>>
}