package com.grouptwo.lokcet.view.feed

import com.google.firebase.Timestamp
import com.grouptwo.lokcet.data.model.User
import com.grouptwo.lokcet.utils.DataState

data class FeedUiState(
    val isNetworkAvailable: Boolean = false,
    val selectedEmoji: String = "",
    val message: String = "",
    val isEmojiPickerVisible: Boolean = false,
    val isSendButtonEnabled: Boolean = false,
    val friendList: DataState<List<User>> = DataState.Loading,
    val selectedFriend: User? = null, // Null means all friends are selected to request feed else only selected friend
    val isRequestingFeed: Boolean = false,
    val currentServerTime: Timestamp? = null
)