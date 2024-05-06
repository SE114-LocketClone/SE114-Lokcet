package com.grouptwo.lokcet.view.chat

import com.grouptwo.lokcet.data.model.ChatRoom
import com.grouptwo.lokcet.data.model.LatestMessage
import com.grouptwo.lokcet.data.model.User
import com.grouptwo.lokcet.utils.DataState

data class ChatUiState(
    val isNetworkAvailable: Boolean = false,
    val chatRoomList: List<ChatRoom> = emptyList(),
    val friendList: DataState<List<User>> = DataState.Loading,
    val friendMap: Map<String, User> = emptyMap(),
    val latestMessageMap: Map<String, LatestMessage> = emptyMap(),
)