package com.grouptwo.lokcet.view.feed

import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.grouptwo.lokcet.data.model.Feed
import com.grouptwo.lokcet.data.model.User
import com.grouptwo.lokcet.di.paging.FeedRepository
import com.grouptwo.lokcet.di.service.AccountService
import com.grouptwo.lokcet.di.service.InternetService
import com.grouptwo.lokcet.di.service.UserService
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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
@OptIn(ExperimentalPagingApi::class)
class FeedViewModel @Inject constructor(
    private val internetService: InternetService,
    private val userService: UserService,
    private val accountService: AccountService,
    private val feedRepository: FeedRepository
) : LokcetViewModel() {
    private val _uiState = MutableStateFlow(FeedUiState())
    private val _feedState = MutableStateFlow<PagingData<Feed>>(PagingData.empty())
    val feedState: StateFlow<PagingData<Feed>> = _feedState.asStateFlow()
    val uiState: StateFlow<FeedUiState> = _uiState
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
                // get server time when network is available
                fetchCurrentServerTime()
                // Make sure to fetch friend list when network is available then start fetching feeds
                fetchFriendList()
            }
        }
    }

    fun requestFeed() {
        launchCatching {
            try {
                if (_uiState.value.isNetworkAvailable.not()) {
                    throw Exception("Không có kết nối mạng")
                }
                _uiState.update {
                    it.copy(isRequestingFeed = true)
                }
                val friendList = _uiState.value.friendList
                if (friendList is DataState.Success) {
                    if (_uiState.value.selectedFriend == null) {
                        // Request feed for all friends in friend list
                        feedRepository.getFeeds(friendList.data.map {
                            it.id
                        }).distinctUntilChanged().cachedIn(viewModelScope).collect {
                            _feedState.value = it
                        }
                    } else {
                        // Request feed for selected friend only
                        Log.d("FeedViewModel", "requestFeed: ${_uiState.value.selectedFriend}")
                        feedRepository.getFeeds(listOf(_uiState.value.selectedFriend!!.id))
                            .distinctUntilChanged().cachedIn(viewModelScope).collect {
                                _feedState.value = it
                            }
                    }
                }
            } catch (e: CancellationException) {
                // Do nothing
            } catch (e: Exception) {
                SnackbarManager.showMessage(e.toSnackbarMessage())
            } finally {
                _uiState.update {
                    it.copy(isRequestingFeed = false)
                }
            }
        }
    }

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
                            // add current user to friend list
                            val currentUser = accountService.getCurrentUser()
                            val friendList = dataState.data.toMutableList()
                            if (currentUser.id.isNotEmpty() && friendList.none { it.id == currentUser.id }) {
                                friendList.add(currentUser)
                            }
                            _uiState.update {
                                it.copy(
                                    friendList = DataState.Success(friendList),
                                    selectedFriend = null
                                )
                            }
                            // Request feed for all friends
                            requestFeed()
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
                    it.copy(
                        friendList = DataState.Error(e), selectedFriend = null
                    )
                }
                SnackbarManager.showMessage(e.toSnackbarMessage())
            }
        }
    }

    fun fetchCurrentServerTime() {
        launchCatching {
            try {
                if (_uiState.value.isNetworkAvailable.not()) {
                    throw Exception("Không có kết nối mạng")
                }
                val currentServerTime = accountService.getCurrentServerTime()
                if (currentServerTime != null) {
                    _uiState.update {
                        it.copy(currentServerTime = currentServerTime)
                    }
                }
            } catch (e: CancellationException) {
                // Do nothing
            } catch (e: Exception) {
                SnackbarManager.showMessage(e.toSnackbarMessage())
            }
        }
    }

    fun onSelectedFriendChanged(user: User?) {
        _uiState.update {
            it.copy(selectedFriend = user)
        }
        // Request feed for selected friend
        requestFeed()
    }

    fun setCurrentUser(userId: String) {
        // find in friend list
        val friendList = _uiState.value.friendList
        if (friendList is DataState.Success) {
            val user = friendList.data.find { it.id == userId }
            _uiState.update {
                it.copy(currentUser = user)
            }
        }
    }

    fun onReplyTextChanged(reply: String) {
        _uiState.update {
            it.copy(reply = reply, isSendButtonEnabled = reply.isNotBlank())
        }
    }

    fun onEmojiSelected(emoji: String) {
        _uiState.update {
            it.copy(selectedEmoji = emoji)
        }
    }
}
