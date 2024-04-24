package com.grouptwo.lokcet.view.add_friend

import com.grouptwo.lokcet.data.model.User
import com.grouptwo.lokcet.utils.DataState

data class AddFriendUiState(
    val searchKeyword: String = "",
    val suggestedList: DataState<List<User>> = DataState.Loading,
    val filteredSuggestedList: DataState<List<User>> = DataState.Loading,
    val isNetworkAvailable: Boolean = false,
    val isAddingFriend: List<Boolean> = emptyList(),
    val hasAddFriendSuccess: List<Boolean> = emptyList()
)