package com.grouptwo.lokcet.view.friend

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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class FriendViewModel @Inject constructor(
    private val userService: UserService,
    private val internetService: InternetService,
    private val accountService: AccountService
) : LokcetViewModel() {
    // Initialize the state of suggest friend list as Loading
    private val _uiState = MutableStateFlow(FriendUiState())
    val uiState: StateFlow<FriendUiState> = _uiState.asStateFlow()
    private val networkStatus: StateFlow<ConnectionState> = internetService.networkStatus.stateIn(
        scope = viewModelScope,
        initialValue = ConnectionState.Unknown,
        started = SharingStarted.WhileSubscribed(500000)
    )

    init {
        launchCatching {
            networkStatus.collect { connectionState ->
                _uiState.update {
                    it.copy(isNetworkAvailable = connectionState == ConnectionState.Available || connectionState == ConnectionState.Unknown)
                }
                launch { fetchSuggestFriendList() }
                launch { fetchWaitedFriendList() }
                launch { fetchRequestFriendList() }
                launch { fetchFriendList() }
            }
        }
    }

    fun fetchSuggestFriendList() {
        launchCatching {
            try {
                // Check if the network is available
                if (_uiState.value.isNetworkAvailable.not()) {
                    throw Exception("Không có kết nối mạng")
                }
                userService.getFriendList().collect { dataState ->
                    when (dataState) {
                        is DataState.Loading -> {
                            _uiState.update {
                                it.copy(
                                    suggestFriendList = DataState.Loading,
                                    filteredSuggestFriendList = DataState.Loading,
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
                                        suggestFriendList = DataState.Success(dataState.data),
                                        filteredSuggestFriendList = DataState.Success(dataState.data),
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
                // Do nothing
            } catch (e: Exception) {

                _uiState.update {
                    it.copy(suggestFriendList = DataState.Error(e),
                        filteredSuggestFriendList = DataState.Error(e),
                        isAddingFriend = List(0) { false },
                        hasAddFriendSuccess = List(0) { false })
                }
                SnackbarManager.showMessage(e.toSnackbarMessage())
            }
        }
    }

    // Waited friend only show when hasRemoveWaitedFriendSuccess is false
    // Has only one action is remove friend request from the wait list
    fun fetchWaitedFriendList() {
        launchCatching {
            try {
                // Check if the network is available
                if (_uiState.value.isNetworkAvailable.not()) {
                    throw Exception("Không có kết nối mạng")
                }
                userService.getWaitedFriendList().collect { dataState ->
                    when (dataState) {
                        is DataState.Loading -> {
                            _uiState.update {
                                it.copy(
                                    waitedFriendList = DataState.Loading,
                                )
                            }
                        }

                        is DataState.Success -> {
                            accountService.currentUser.collect { user ->
                                val friendWaitList = user.friendWaitList
                                // Check if the friend is in the wait list
                                val updateHasRemovedFriend = dataState.data.map { friend ->
                                    friendWaitList.contains(friend.id).not()
                                }
                                _uiState.update {
                                    it.copy(
                                        waitedFriendList = DataState.Success(dataState.data),
                                        isRemovingWaitedFriend = List(dataState.data.size) { false },
                                        hasRemoveWaitedFriendSuccess = updateHasRemovedFriend,
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
                // Do nothing
            } catch (e: Exception) {

                _uiState.update {
                    it.copy(waitedFriendList = DataState.Error(e),
                        isRemovingWaitedFriend = List(0) { false },
                        hasRemoveWaitedFriendSuccess = List(0) { false })
                }
                SnackbarManager.showMessage(e.toSnackbarMessage())
            }
        }
    }

    // fetch the request friend list
    // Has two actions: accept friend request and remove friend request
    // The friend request only show when hasAcceptFriendSuccess is false or hasRemoveFriendSuccess is false
    fun fetchRequestFriendList() {
        launchCatching {
            try {
                if (_uiState.value.isNetworkAvailable.not()) {
                    throw Exception("Không có kết nối mạng")
                }
                userService.getRequestFriendList().collect { dataState ->
                    when (dataState) {
                        is DataState.Loading -> {
                            _uiState.update {
                                it.copy(
                                    requestedFriendList = DataState.Loading,
                                )
                            }
                        }

                        is DataState.Success -> {
                            accountService.currentUser.collect { user ->
                                val friendRequestList = user.friendRequests
                                val updatedHasAcceptRequestFriendSuccess =
                                    dataState.data.map { friend ->
                                        friendRequestList.contains(friend.id)
                                    }
                                val updatedHasRemoveRequestedFriendSuccess =
                                    dataState.data.map { friend ->
                                        friendRequestList.contains(friend.id).not()
                                    }
                                _uiState.update {
                                    it.copy(
                                        requestedFriendList = DataState.Success(dataState.data),
                                        isAcceptingRequestFriend = List(dataState.data.size) { false },
                                        hasAcceptRequestFriendSuccess = updatedHasAcceptRequestFriendSuccess,
                                        isRemovingRequestedFriend = List(dataState.data.size) { false },
                                        hasRemoveRequestedFriendSuccess = updatedHasRemoveRequestedFriendSuccess
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
                // Do nothing
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(requestedFriendList = DataState.Error(e),
                        isAcceptingRequestFriend = List(0) { false },
                        hasAcceptRequestFriendSuccess = List(0) { false },
                        isRemovingRequestedFriend = List(0) { false },
                        hasRemoveRequestedFriendSuccess = List(0) { false })
                }
                SnackbarManager.showMessage(e.toSnackbarMessage())
            }
        }
    }

    // Get the friend list
    // Has only one action is remove friend from the friend list
    // The friend list only show when hasRemoveFriendSuccess is false
    fun fetchFriendList() {
        launchCatching {
            try {
                if (_uiState.value.isNetworkAvailable.not()) {
                    throw Exception("Không có kết nối mạng")
                }
                userService.getFriendList().collect { dataState ->
                    when (dataState) {
                        is DataState.Loading -> {
                            _uiState.update {
                                it.copy(
                                    friendList = DataState.Loading,
                                )
                            }
                        }

                        is DataState.Success -> {
                            accountService.currentUser.collect { user ->
                                val friendList = user.friends
                                val updatedHasRemoveFriendSuccess = dataState.data.map { friend ->
                                    friendList.contains(friend.id).not()
                                }
                                _uiState.update {
                                    it.copy(
                                        friendList = DataState.Success(dataState.data),
                                        isRemovingFriend = List(dataState.data.size) { false },
                                        hasRemoveFriendSuccess = updatedHasRemoveFriendSuccess
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
                // Do nothing
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(friendList = DataState.Error(e),
                        isRemovingFriend = List(0) { false },
                        hasRemoveFriendSuccess = List(0) { false })
                }
                SnackbarManager.showMessage(e.toSnackbarMessage())
            }
        }
    }

    fun onSearchChange(keyword: String) {
        _uiState.update {
            it.copy(searchKeyword = keyword)
        }
    }

    fun performSearch(keyword: String) {
        val suggestUserList = _uiState.value.suggestFriendList
        if (suggestUserList is DataState.Success) {
            _uiState.update {
                it.copy(filteredSuggestFriendList = DataState.Success(suggestUserList.data.filter { user ->
                    user.firstName.contains(
                        keyword, ignoreCase = true
                    ) || user.lastName.contains(
                        keyword, ignoreCase = true
                    )
                }))
            }
        }
    }

    fun onRetryAll() {
        launchCatching {
            launch {
                fetchSuggestFriendList()
            }
            launch {
                fetchWaitedFriendList()
            }
            launch {
                fetchRequestFriendList()
            }
            launch {
                fetchFriendList()
            }
        }
    }

    fun onBackClick(clearAndNavigate: (String) -> Unit) {
        clearAndNavigate(Screen.HomeScreen_1.route)
    }

    fun onAddFriendClick(friend: User) {
        launchCatching {
            try {
                if (_uiState.value.isNetworkAvailable.not()) {
                    throw Exception("Không có kết nối mạng")
                }
                val userId = accountService.currentUserId
                val friendId = friend.id
                userService.addFriend(userId, friendId).collect { dataState ->
                    when (dataState) {
                        is DataState.Loading -> {
                            val currentList = _uiState.value.suggestFriendList
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
                            val currentList = _uiState.value.suggestFriendList
                            if (currentList is DataState.Success) {
                                val index = currentList.data.indexOf(friend)
                                if (index != -1) {
                                    _uiState.update {
                                        val updatedIsAddingFriend =
                                            it.isAddingFriend.toMutableList()
                                        val updatedHasAddFriendSuccess =
                                            it.hasAddFriendSuccess.toMutableList()
                                        val updatedIsRemoveWaitedFriend =
                                            it.isRemovingWaitedFriend.toMutableList()
                                        val updatedHasRemoveWaitedFriendSuccess =
                                            it.hasRemoveWaitedFriendSuccess.toMutableList()
                                        // Remove friend from the suggest friend list and add to the wait list
                                        var waitList = _uiState.value.waitedFriendList
                                        if (waitList is DataState.Success) {
                                            waitList =
                                                DataState.Success(waitList.data.toMutableList()
                                                    .apply { add(friend) })
                                            updatedHasRemoveWaitedFriendSuccess.add(false)
                                            updatedIsRemoveWaitedFriend.add(false)
                                        }
                                        updatedIsAddingFriend[index] = false
                                        updatedHasAddFriendSuccess[index] = true

                                        it.copy(
                                            isAddingFriend = updatedIsAddingFriend,
                                            hasAddFriendSuccess = updatedHasAddFriendSuccess,
                                            waitedFriendList = waitList,
                                            isRemovingWaitedFriend = updatedIsRemoveWaitedFriend,
                                            hasRemoveWaitedFriendSuccess = updatedHasRemoveWaitedFriendSuccess
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
            } catch (e: CancellationException) {
                // Do nothing
            } catch (e: Exception) {
                val currentList = _uiState.value.suggestFriendList
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
                SnackbarManager.showMessage(e.toSnackbarMessage())
            }
        }
    }

    fun onRemoveFriend(friend: User) {
        launchCatching {
            try {
                if (_uiState.value.isNetworkAvailable.not()) {
                    throw Exception("Không có kết nối mạng")
                }
                val userId = accountService.currentUserId
                val friendId = friend.id
                userService.removeFriend(userId, friendId).collect { dataState ->
                    when (dataState) {
                        is DataState.Loading -> {
                            val currentList = _uiState.value.friendList
                            if (currentList is DataState.Success) {
                                val index = currentList.data.indexOf(friend)
                                if (index != -1) {
                                    _uiState.update {
                                        val updatedIsRemovingFriend =
                                            it.isRemovingFriend.toMutableList()
                                        updatedIsRemovingFriend[index] = true
                                        it.copy(isRemovingFriend = updatedIsRemovingFriend)
                                    }
                                }
                            }
                        }

                        is DataState.Success -> {
                            val currentList = _uiState.value.friendList
                            if (currentList is DataState.Success) {
                                val index = currentList.data.indexOf(friend)
                                if (index != -1) {
                                    _uiState.update {
                                        val updatedIsRemovingFriend =
                                            it.isRemovingFriend.toMutableList()
                                        val updatedHasRemoveFriendSuccess =
                                            it.hasRemoveFriendSuccess.toMutableList()
                                        val updatedFriendList = currentList.data.toMutableList()
                                        updatedIsRemovingFriend.removeAt(index)
                                        updatedHasRemoveFriendSuccess.removeAt(index)
                                        updatedFriendList.removeAt(index)
                                        it.copy(
                                            isRemovingFriend = updatedIsRemovingFriend,
                                            hasRemoveFriendSuccess = updatedHasRemoveFriendSuccess,
                                            friendList = DataState.Success(updatedFriendList)
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
            } catch (e: CancellationException) {
                // Do nothing
            } catch (e: Exception) {
                val currentList = _uiState.value.friendList
                if (currentList is DataState.Success) {
                    val index = currentList.data.indexOf(friend)
                    if (index != -1) {
                        _uiState.update {
                            val updatedIsRemovingFriend = it.isRemovingFriend.toMutableList()
                            val updatedHasRemoveFriendSuccess =
                                it.hasRemoveFriendSuccess.toMutableList()
                            updatedIsRemovingFriend[index] = false
                            updatedHasRemoveFriendSuccess[index] = false
                            it.copy(
                                isRemovingFriend = updatedIsRemovingFriend,
                                hasRemoveFriendSuccess = updatedHasRemoveFriendSuccess
                            )
                        }
                    }

                }
            }
        }
    }

    fun onAcceptFriend(friend: User) {
        launchCatching {
            try {
                if (_uiState.value.isNetworkAvailable.not()) {
                    throw Exception("Không có kết nối mạng")
                }
                val userId = accountService.currentUserId
                val friendId = friend.id
                userService.acceptFriend(userId, friendId).collect { dataState ->
                    when (dataState) {
                        is DataState.Loading -> {
                            val currentList = _uiState.value.requestedFriendList
                            if (currentList is DataState.Success) {
                                val index = currentList.data.indexOf(friend)
                                if (index != -1) {
                                    _uiState.update {
                                        val updatedIsAcceptingRequestFriend =
                                            it.isAcceptingRequestFriend.toMutableList()
                                        updatedIsAcceptingRequestFriend[index] = true
                                        it.copy(isAcceptingRequestFriend = updatedIsAcceptingRequestFriend)
                                    }
                                }
                            }
                        }

                        is DataState.Success -> {
                            val currentList = _uiState.value.requestedFriendList
                            if (currentList is DataState.Success) {
                                val index = currentList.data.indexOf(friend)
                                if (index != -1) {
                                    _uiState.update {
                                        val updatedIsAcceptingRequestFriend =
                                            it.isAcceptingRequestFriend.toMutableList()
                                        val updatedHasAcceptRequestFriendSuccess =
                                            it.hasAcceptRequestFriendSuccess.toMutableList()
                                        val updatedIsRemovingRequestedFriend =
                                            it.isRemovingRequestedFriend.toMutableList()
                                        val updatedHasRemoveRequestedFriendSuccess =
                                            it.hasRemoveRequestedFriendSuccess.toMutableList()
                                        val updatedRequestFriendList =
                                            currentList.data.toMutableList()

                                        // Remove friend from the requested friend list
                                        updatedRequestFriendList.removeAt(index)
                                        updatedIsAcceptingRequestFriend.removeAt(index)
                                        updatedHasAcceptRequestFriendSuccess.removeAt(index)
                                        updatedIsRemovingRequestedFriend.removeAt(index)
                                        updatedHasRemoveRequestedFriendSuccess.removeAt(index)
                                        // Add friend to the friend list
                                        val updatedFriendList = it.friendList
                                        val updatedRemoveFriendList =
                                            it.isRemovingFriend.toMutableList()
                                        val updatedHasRemoveFriendSuccess =
                                            it.hasRemoveFriendSuccess.toMutableList()
                                        if (updatedFriendList is DataState.Success) {
                                            updatedFriendList.data.toMutableList().apply {
                                                add(friend)
                                            }
                                            updatedRemoveFriendList.add(false)
                                            updatedHasRemoveFriendSuccess.add(false)
                                            it.copy(
                                                isAcceptingRequestFriend = updatedIsAcceptingRequestFriend,
                                                hasAcceptRequestFriendSuccess = updatedHasAcceptRequestFriendSuccess,
                                                isRemovingRequestedFriend = updatedIsRemovingRequestedFriend,
                                                hasRemoveRequestedFriendSuccess = updatedHasRemoveRequestedFriendSuccess,
                                                friendList = DataState.Success(updatedFriendList.data),
                                                isRemovingFriend = updatedRemoveFriendList,
                                                hasRemoveFriendSuccess = updatedHasRemoveFriendSuccess,
                                                requestedFriendList = DataState.Success(
                                                    updatedRequestFriendList
                                                )
                                            )
                                        } else {
                                            it.copy(
                                                isAcceptingRequestFriend = updatedIsAcceptingRequestFriend,
                                                hasAcceptRequestFriendSuccess = updatedHasAcceptRequestFriendSuccess,
                                                isRemovingRequestedFriend = updatedIsRemovingRequestedFriend,
                                                hasRemoveRequestedFriendSuccess = updatedHasRemoveRequestedFriendSuccess,
                                                requestedFriendList = DataState.Success(
                                                    updatedRequestFriendList
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        is DataState.Error -> {
                            throw dataState.exception
                        }
                    }
                }
            } catch (e: CancellationException) {
                // Do nothing
            } catch (e: Exception) {
                val currentList = _uiState.value.requestedFriendList
                if (currentList is DataState.Success) {
                    val index = currentList.data.indexOf(friend)
                    if (index != -1) {
                        _uiState.update {
                            val updatedIsAcceptingRequestFriend =
                                it.isAcceptingRequestFriend.toMutableList()
                            val updatedHasAcceptRequestFriendSuccess =
                                it.hasAcceptRequestFriendSuccess.toMutableList()
                            updatedIsAcceptingRequestFriend[index] = false
                            updatedHasAcceptRequestFriendSuccess[index] = false
                            it.copy(
                                isAcceptingRequestFriend = updatedIsAcceptingRequestFriend,
                                hasAcceptRequestFriendSuccess = updatedHasAcceptRequestFriendSuccess
                            )
                        }
                    }
                }
            }
        }
    }

    fun onRejectFriend(friend: User) {
        launchCatching {
            try {
                if (_uiState.value.isNetworkAvailable.not()) {
                    throw Exception("Không có kết nối mạng")
                }
                val userId = accountService.currentUserId
                val friendId = friend.id
                userService.rejectFriend(userId, friendId).collect { dataState ->
                    when (dataState) {
                        is DataState.Loading -> {
                            val currentList = _uiState.value.requestedFriendList
                            if (currentList is DataState.Success) {
                                val index = currentList.data.indexOf(friend)
                                if (index != -1) {
                                    _uiState.update {
                                        val updatedIsRemovingRequestedFriend =
                                            it.isRemovingRequestedFriend.toMutableList()
                                        updatedIsRemovingRequestedFriend[index] = true
                                        it.copy(isRemovingRequestedFriend = updatedIsRemovingRequestedFriend)
                                    }
                                }
                            }
                        }

                        is DataState.Success -> {
                            val currentList = _uiState.value.requestedFriendList
                            if (currentList is DataState.Success) {
                                val index = currentList.data.indexOf(friend)
                                if (index != -1) {
                                    _uiState.update {
                                        val updatedIsAcceptingRequestFriend =
                                            it.isAcceptingRequestFriend.toMutableList()
                                        val updatedHasAcceptRequestFriendSuccess =
                                            it.hasAcceptRequestFriendSuccess.toMutableList()
                                        val updatedIsRemovingRequestedFriend =
                                            it.isRemovingRequestedFriend.toMutableList()
                                        val updatedHasRemoveRequestedFriendSuccess =
                                            it.hasRemoveRequestedFriendSuccess.toMutableList()
                                        val updatedRequestFriendList =
                                            currentList.data.toMutableList()

                                        // Remove friend from the requested friend list
                                        updatedRequestFriendList.removeAt(index)
                                        updatedIsAcceptingRequestFriend.removeAt(index)
                                        updatedHasAcceptRequestFriendSuccess.removeAt(index)
                                        updatedIsRemovingRequestedFriend.removeAt(index)
                                        updatedHasRemoveRequestedFriendSuccess.removeAt(index)
                                        it.copy(
                                            isAcceptingRequestFriend = updatedIsAcceptingRequestFriend,
                                            hasAcceptRequestFriendSuccess = updatedHasAcceptRequestFriendSuccess,
                                            isRemovingRequestedFriend = updatedIsRemovingRequestedFriend,
                                            hasRemoveRequestedFriendSuccess = updatedHasRemoveRequestedFriendSuccess,
                                            requestedFriendList = DataState.Success(
                                                updatedRequestFriendList
                                            )
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
            } catch (e: CancellationException) {
                // Do nothing
            } catch (e: Exception) {
                val currentList = _uiState.value.requestedFriendList
                if (currentList is DataState.Success) {
                    val index = currentList.data.indexOf(friend)
                    if (index != -1) {
                        _uiState.update {
                            val updatedIsRemovingRequestedFriend =
                                it.isRemovingRequestedFriend.toMutableList()
                            val updatedHasRemoveRequestedFriendSuccess =
                                it.hasRemoveRequestedFriendSuccess.toMutableList()
                            updatedIsRemovingRequestedFriend[index] = false
                            updatedHasRemoveRequestedFriendSuccess[index] = false
                            it.copy(
                                isRemovingRequestedFriend = updatedIsRemovingRequestedFriend,
                                hasRemoveRequestedFriendSuccess = updatedHasRemoveRequestedFriendSuccess
                            )
                        }
                    }
                }
            }
        }
    }

    fun onRemoveFromWaitList(friend: User) {
        launchCatching {
            try {
                if (_uiState.value.isNetworkAvailable.not()) {
                    throw Exception("Không có kết nối mạng")
                }
                val userId = accountService.currentUserId
                val friendId = friend.id
                userService.removeWaitedFriend(userId, friendId).collect { dataState ->
                    when (dataState) {
                        is DataState.Loading -> {
                            val currentList = _uiState.value.waitedFriendList
                            if (currentList is DataState.Success) {
                                val index = currentList.data.indexOf(friend)
                                if (index != -1) {
                                    _uiState.update {
                                        val updatedIsRemovingWaitedFriend =
                                            it.isRemovingWaitedFriend.toMutableList()
                                        updatedIsRemovingWaitedFriend[index] = true
                                        it.copy(isRemovingWaitedFriend = updatedIsRemovingWaitedFriend)
                                    }
                                }
                            }
                        }

                        is DataState.Success -> {
                            val currentList = _uiState.value.waitedFriendList
                            if (currentList is DataState.Success) {
                                val index = currentList.data.indexOf(friend)
                                if (index != -1) {
                                    _uiState.update {
                                        val updatedIsRemovingWaitedFriend =
                                            it.isRemovingWaitedFriend.toMutableList()
                                        val updatedHasRemoveWaitedFriendSuccess =
                                            it.hasRemoveWaitedFriendSuccess.toMutableList()
                                        val waitedFriendList = currentList.data.toMutableList()
                                        updatedIsRemovingWaitedFriend.removeAt(index)
                                        updatedHasRemoveWaitedFriendSuccess.removeAt(index)
                                        waitedFriendList.removeAt(index)
                                        // Add friend to the suggest friend list
                                        val updatedSuggestFriendList = it.suggestFriendList
                                        val updatedIsAddingFriend =
                                            it.isAddingFriend.toMutableList()
                                        val updatedHasAddFriendSuccess =
                                            it.hasAddFriendSuccess.toMutableList()
                                        if (updatedSuggestFriendList is DataState.Success) {
                                            updatedSuggestFriendList.data.toMutableList().apply {
                                                add(friend)
                                            }
                                            updatedIsAddingFriend.add(false)
                                            updatedHasAddFriendSuccess.add(false)
                                            it.copy(
                                                isRemovingWaitedFriend = updatedIsRemovingWaitedFriend,
                                                hasRemoveWaitedFriendSuccess = updatedHasRemoveWaitedFriendSuccess,
                                                suggestFriendList = DataState.Success(
                                                    updatedSuggestFriendList.data
                                                ),
                                                isAddingFriend = updatedIsAddingFriend,
                                                hasAddFriendSuccess = updatedHasAddFriendSuccess,
                                                waitedFriendList = DataState.Success(
                                                    waitedFriendList
                                                )
                                            )
                                        } else {
                                            it.copy(
                                                isRemovingWaitedFriend = updatedIsRemovingWaitedFriend,
                                                hasRemoveWaitedFriendSuccess = updatedHasRemoveWaitedFriendSuccess,
                                                waitedFriendList = DataState.Success(
                                                    waitedFriendList
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        is DataState.Error -> {
                            throw dataState.exception
                        }
                    }
                }
            } catch (e: CancellationException) {
                // Do nothing
            } catch (e: Exception) {
                val currentList = _uiState.value.waitedFriendList
                if (currentList is DataState.Success) {
                    val index = currentList.data.indexOf(friend)
                    if (index != -1) {
                        _uiState.update {
                            val updatedIsRemovingWaitedFriend =
                                it.isRemovingWaitedFriend.toMutableList()
                            val updatedHasRemoveWaitedFriendSuccess =
                                it.hasRemoveWaitedFriendSuccess.toMutableList()
                            updatedIsRemovingWaitedFriend[index] = false
                            updatedHasRemoveWaitedFriendSuccess[index] = false
                            it.copy(
                                isRemovingWaitedFriend = updatedIsRemovingWaitedFriend,
                                hasRemoveWaitedFriendSuccess = updatedHasRemoveWaitedFriendSuccess
                            )
                        }
                    }
                }
            }
        }
    }
}
