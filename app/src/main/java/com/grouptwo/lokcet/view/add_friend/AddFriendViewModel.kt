package com.grouptwo.lokcet.view.add_friend

import androidx.lifecycle.viewModelScope
import com.grouptwo.lokcet.data.model.User
import com.grouptwo.lokcet.di.service.AccountService
import com.grouptwo.lokcet.di.service.InternetService
import com.grouptwo.lokcet.di.service.UserService
import com.grouptwo.lokcet.navigation.Screen
import com.grouptwo.lokcet.ui.component.global.snackbar.SnackbarManager
import com.grouptwo.lokcet.ui.component.global.snackbar.SnackbarMessage.Companion.toSnackbarMessage
import com.grouptwo.lokcet.utils.ConnectionState
import com.grouptwo.lokcet.utils.DataState
import com.grouptwo.lokcet.view.LokcetViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class AddFriendViewModel @Inject constructor(
    private val userService: UserService,
    private val internetService: InternetService,
    private val accountService: AccountService
) : LokcetViewModel() {
    // Initialize the state of suggest friend list as Loading
    private val _uiState = MutableStateFlow(AddFriendUiState())
    val uiState: StateFlow<AddFriendUiState> = _uiState.asStateFlow()
    private val networkStatus: StateFlow<ConnectionState> = internetService.networkStatus.stateIn(
        scope = viewModelScope,
        initialValue = ConnectionState.Unknown,
        started = WhileSubscribed(500000)
    )

    init {
        launchCatching {
            networkStatus.collect { connectionState ->
                _uiState.update {
                    it.copy(isNetworkAvailable = connectionState == ConnectionState.Available || connectionState == ConnectionState.Unknown)
                }
                // When the state changes then try to fetch the suggest friend list
                // Make sủe get the new state from the networkStatus flow
                fetchSuggestFriendList()
            }
        }
    }

    fun fetchSuggestFriendList() {
        // Fetch the suggest friend list from the server
        launchCatching {
            try {
                // Check if the network is available
                if (_uiState.value.isNetworkAvailable.not()) {
                    throw Exception("Không có kết nối mạng")
                }
                userService.getSuggestFriendList().collect { dataState ->
                    when (dataState) {
                        is DataState.Loading -> {
                            _uiState.update {
                                it.copy(
                                    suggestedList = DataState.Loading,
                                    filteredSuggestedList = DataState.Loading
                                )
                            }
                        }

                        is DataState.Success -> {
                            accountService.currentUser.collect { user ->
                                val friendWaitList = user.friendWaitList
                                val updatedHasAddFriendSuccess = dataState.data.map { friend ->
                                    friendWaitList.contains(friend.id)
                                }
                                _uiState.update {
                                    it.copy(
                                        suggestedList = DataState.Success(dataState.data),
                                        filteredSuggestedList = DataState.Success(dataState.data),
                                        isAddingFriend = List(dataState.data.size) { false },
                                        hasAddFriendSuccess = updatedHasAddFriendSuccess
                                    )
                                }
                            }
                        }

                        is DataState.Error -> {
                            throw dataState.exception
                        }
                    }
                }
            } catch (e: CancellationException) {
                // DO nothing when the coroutine is cancelled
            } catch (e: Exception) {
                // Update the state to Error if there is an exception
                _uiState.update {
                    it.copy(suggestedList = DataState.Error(e),
                        filteredSuggestedList = DataState.Error(e),
                        isAddingFriend = List(0) { false },
                        hasAddFriendSuccess = List(0) { false })
                }
                // Show snackbar with throwable message if there is an exception for UX
                SnackbarManager.showMessage(e.toSnackbarMessage())

            }
        }
    }

    fun onSearchKeywordChanged(keyword: String) {
        // Update the search keyword in the state
        _uiState.update {
            it.copy(searchKeyword = keyword)
        }
    }

    fun performSearch(keyword: String) {
        // Filter the suggest friend list based on the keyword
        val currentList = _uiState.value.suggestedList
        if (currentList is DataState.Success) {
            val filteredList = currentList.data.filter { user ->
                user.firstName.contains(keyword, ignoreCase = true) || user.lastName.contains(
                    keyword, ignoreCase = true
                )
            }
            _uiState.update {
                it.copy(filteredSuggestedList = DataState.Success(filteredList))
            }
        }
    }

    fun onContinueClick(clearAndNavigate: (String) -> Unit) {
        clearAndNavigate(Screen.HomeScreen_1.route)
    }

    fun onAddFriendClick(
        friend: User
    ) {
        // Add friend to the friend list
        launchCatching {
            try {
                // Check if the network is available
                if (_uiState.value.isNetworkAvailable.not()) {
                    throw Exception("Không có kết nối mạng")
                }
                val userId = accountService.currentUserId
                val friendId = friend.id
                userService.addFriend(userId, friendId).collect { dataState ->
                    when (dataState) {
                        is DataState.Loading -> {
                            //  Get the selected friend index and update the state to Loading
                            val currentList = _uiState.value.suggestedList
                            if (currentList is DataState.Success) {
                                val index = currentList.data.indexOf(friend)
                                if (index != -1) {
                                    _uiState.update {
                                        val updatedIsAddingFriend =
                                            it.isAddingFriend.toMutableList()
                                        updatedIsAddingFriend[index] = true
                                        it.copy(isAddingFriend = updatedIsAddingFriend)
                                    }
                                }
                            }
                        }

                        is DataState.Success -> {
                            val currentList = _uiState.value.suggestedList
                            if (currentList is DataState.Success) {
                                val index = currentList.data.indexOf(friend)
                                if (index != -1) {
                                    _uiState.update {
                                        val updatedIsAddingFriend =
                                            it.isAddingFriend.toMutableList()
                                        val updatedHasAddFriendSuccess =
                                            it.hasAddFriendSuccess.toMutableList()
                                        updatedIsAddingFriend[index] = false
                                        updatedHasAddFriendSuccess[index] = true
                                        it.copy(
                                            isAddingFriend = updatedIsAddingFriend,
                                            hasAddFriendSuccess = updatedHasAddFriendSuccess
                                        )
                                    }
                                }
                            }
                        }

                        is DataState.Error -> {
                            throw dataState.exception
                        }
                    }
                }
            } catch (e: Exception) {
                val currentList = _uiState.value.suggestedList
                if (currentList is DataState.Success) {
                    val index = currentList.data.indexOf(friend)
                    if (index != -1) {
                        _uiState.update {
                            val updatedIsAddingFriend = it.isAddingFriend.toMutableList()
                            val updatedHasAddFriendSuccess = it.hasAddFriendSuccess.toMutableList()
                            updatedIsAddingFriend[index] = false
                            updatedHasAddFriendSuccess[index] = false
                            it.copy(
                                isAddingFriend = updatedIsAddingFriend,
                                hasAddFriendSuccess = updatedHasAddFriendSuccess
                            )
                        }
                    }
                }
                // Show snackbar with throwable message if there is an exception for UX
                SnackbarManager.showMessage(e.toSnackbarMessage())
            }
        }
    }
}