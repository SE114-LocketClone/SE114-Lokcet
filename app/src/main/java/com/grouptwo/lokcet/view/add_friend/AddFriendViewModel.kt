package com.grouptwo.lokcet.view.add_friend

import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddFriendViewModel @Inject constructor(
    private val userService: UserService,
    private val internetService: InternetService
) : LokcetViewModel() {
    // Initialize the state of suggest friend list as Loading
    private val _uiState = MutableStateFlow(AddFriendUiState())
    val uiState: StateFlow<AddFriendUiState> = _uiState.asStateFlow()
    private val networkStatus: StateFlow<ConnectionState> = internetService.networkStatus.stateIn(
        scope = viewModelScope,
        initialValue = ConnectionState.Unknown,
        started = WhileSubscribed(5000)
    )

    init {
        viewModelScope.launch {
            networkStatus.collect { connectionState ->
                _uiState.update {
                    it.copy(isNetworkAvailable = connectionState == ConnectionState.Available)
                }
                // When the state changes then try to fetch the suggest friend list
                // Make sủe get the new state from the networkStatus flow
                if (connectionState != ConnectionState.Unknown)
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
                            _uiState.update {
                                it.copy(
                                    suggestedList = DataState.Success(dataState.data),
                                    filteredSuggestedList = DataState.Success(dataState.data)
                                )
                            }
                        }

                        is DataState.Error -> {
                            _uiState.update {
                                it.copy(
                                    suggestedList = DataState.Error(dataState.exception),
                                    filteredSuggestedList = DataState.Error(dataState.exception)
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                // Update the state to Error if there is an exception
                _uiState.update {
                    it.copy(
                        suggestedList = DataState.Error(e),
                        filteredSuggestedList = DataState.Error(e)
                    )
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
        clearAndNavigate(Screen.AddWidgetTutorialScreen.route)
    }
}