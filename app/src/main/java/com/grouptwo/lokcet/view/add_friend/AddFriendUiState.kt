package com.grouptwo.lokcet.view.add_friend

import com.grouptwo.lokcet.data.model.User
import com.grouptwo.lokcet.utils.DataState
import kotlinx.coroutines.flow.MutableStateFlow

data class AddFriendUiState(
    val searchKeyword: String = "",
    val suggestedList: DataState<List<User>> = DataState.Loading,
    val filteredSuggestedList: DataState<List<User>> = DataState.Loading,
    val isNetworkAvailable: Boolean = false
)