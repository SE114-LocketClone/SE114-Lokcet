package com.grouptwo.lokcet.view.friend

import com.grouptwo.lokcet.data.model.User
import com.grouptwo.lokcet.utils.DataState

data class FriendUiState(
    val isNetworkAvailable: Boolean = false,
    val searchKeyword: String = "",

    val suggestFriendList: DataState<List<User>> = DataState.Loading,
    val filteredSuggestFriendList: DataState<List<User>> = DataState.Loading,
    val isAddingFriend: List<Boolean> = emptyList(),

    val waitedFriendList: DataState<List<User>> = DataState.Loading,
    val isRemovingWaitedFriend: List<Boolean> = emptyList(),

    val friendList: DataState<List<User>> = DataState.Loading,
    val isRemovingFriend: List<Boolean> = emptyList(),

    val requestedFriendList: DataState<List<User>> = DataState.Loading,
    val isAcceptingRequestFriend: List<Boolean> = emptyList(),
    val isRemovingRequestedFriend: List<Boolean> = emptyList(),
)