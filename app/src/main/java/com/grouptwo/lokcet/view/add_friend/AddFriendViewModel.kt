package com.grouptwo.lokcet.view.add_friend

import androidx.compose.runtime.mutableStateOf
import com.grouptwo.lokcet.data.model.User
import com.grouptwo.lokcet.di.service.UserService
import com.grouptwo.lokcet.navigation.Screen
import com.grouptwo.lokcet.ui.component.global.snackbar.SnackbarManager
import com.grouptwo.lokcet.ui.component.global.snackbar.SnackbarMessage.Companion.toSnackbarMessage
import com.grouptwo.lokcet.utils.DataState
import com.grouptwo.lokcet.view.LokcetViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AddFriendViewModel @Inject constructor(
    private val userService: UserService
) : LokcetViewModel() {
    // Initialize the state of suggest friend list as Loading
    private val _suggestedList = MutableStateFlow<DataState<List<User>>>(DataState.Loading)
    private val _filteredSuggestedList = MutableStateFlow<DataState<List<User>>>(DataState.Loading)

    // State Flow for suggest friend list
    val suggestedList: StateFlow<DataState<List<User>>> = _filteredSuggestedList.asStateFlow()

    var uiState = mutableStateOf(AddFriendUiState())
        private set

    init {
        fetchSuggestFriendList()
    }

    fun fetchSuggestFriendList() {
        // Fetch the suggest friend list from the server
        launchCatching {
            try {
                userService.getSuggestFriendList().collect { dataState ->
                    when (dataState) {
                        is DataState.Loading -> {
                            _suggestedList.value = DataState.Loading
                        }

                        is DataState.Success -> {
                            _suggestedList.value = DataState.Success(dataState.data)
                            // Update filtered list when suggested list is updated
                            _filteredSuggestedList.value = _suggestedList.value
                        }

                        is DataState.Error -> {
                            _suggestedList.value = DataState.Error(dataState.exception)
                            // Update filtered list when suggested list is updated
                            _filteredSuggestedList.value = _suggestedList.value
                        }
                    }
                }
            } catch (e: Exception) {
                // Update the state to Error if there is an exception
                _suggestedList.value = DataState.Error(e)
                // Show snackbar with throwable message if there is an exception for UX
                SnackbarManager.showMessage(e.toSnackbarMessage())

            }
        }
    }

    fun onSearchKeywordChanged(keyword: String) {
        // Update the search keyword in the state
        uiState.value = uiState.value.copy(searchKeyword = keyword)
    }

    fun performSearch(keyword: String) {
        // Filter the suggest friend list based on the keyword
        val currentList = _suggestedList.value
        if (currentList is DataState.Success) {
            val filteredList = currentList.data.filter { user ->
                user.firstName.contains(keyword, ignoreCase = true) || user.lastName.contains(
                    keyword,
                    ignoreCase = true
                )
            }
            _filteredSuggestedList.value = DataState.Success(filteredList)
        }
    }

    fun onContinueClick(clearAndNavigate: (String) -> Unit) {
        clearAndNavigate(Screen.AddWidgetTutorialScreen.route)
    }
}