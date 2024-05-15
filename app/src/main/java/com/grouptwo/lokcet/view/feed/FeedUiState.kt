package com.grouptwo.lokcet.view.feed

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.paging.PagingData
import com.google.firebase.Timestamp
import com.grouptwo.lokcet.data.model.EmojiReaction
import com.grouptwo.lokcet.data.model.Feed
import com.grouptwo.lokcet.data.model.User
import com.grouptwo.lokcet.utils.DataState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class FeedUiState(
    val isNetworkAvailable: Boolean = false,
    val selectedEmoji: String = "",
    val reply: String = "",
    val isSendButtonEnabled: Boolean = false,
    val friendList: DataState<List<User>> = DataState.Loading,
    val selectedFriend: User? = null, // Null means all friends are selected to request feed else only selected friend
    val isRequestingFeed: Boolean = false,
    val currentServerTime: Timestamp? = null,
    val searchText: String = "",
    val feedState: PagingData<Feed> = PagingData.empty(),
    val isEmojiPickerVisible: Boolean= false,
    val isShowReplyTextField: Boolean = false,
    val ownerUser : User? = null,
    val isShowReactionList: Boolean = false,
    val curentUserReactList: List<EmojiReaction> = emptyList(),
    val friendAvatar: Map<String, String> = emptyMap(),
    val showOptionMenu: Boolean = false,
    val isShowGridView: Boolean = false,
    val isShowDeleteDialog: Boolean = false,
)