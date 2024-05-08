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
import com.grouptwo.lokcet.di.service.ChatService
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
    private val feedRepository: FeedRepository,
    private val chatService: ChatService
) : LokcetViewModel() {
    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()
    private val _feedState = MutableStateFlow<PagingData<Feed>>(PagingData.empty())
    val feedState: StateFlow<PagingData<Feed>> = _feedState.asStateFlow()
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
                            // Create avatar map for friend list
                            val friendAvatar =
                                friendList.associateBy({ it.id }, { it.profilePicture })
                            if (currentUser.id.isNotEmpty() && friendList.none { it.id == currentUser.id }) {
                                friendList.add(currentUser)
                                _uiState.update {
                                    it.copy(ownerUser = currentUser)
                                }
                            }
                            _uiState.update {
                                it.copy(
                                    friendList = DataState.Success(friendList),
                                    selectedFriend = null,
                                    friendAvatar = friendAvatar
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

    fun onReplyTextChanged(reply: String) {
        _uiState.update {
            it.copy(reply = reply, isSendButtonEnabled = reply.isNotBlank())
        }
    }

    fun onSendReply(feed: Feed) {
        if (_uiState.value.isSendButtonEnabled.not()) {
            return
        }
        launchCatching {
            try {
                if (_uiState.value.isNetworkAvailable.not()) {
                    throw Exception("Không có kết nối mạng")
                }
                val currentUserId = accountService.currentUserId
                val chatRoomId =
                    if (currentUserId < feed.uploadImage.userId) "${currentUserId}_${feed.uploadImage.userId}" else "${feed.uploadImage.userId}_$currentUserId"
                // Call API to reply feed
                chatService.sendReplyMessage(
                    chatRoomId,
                    _uiState.value.reply,
                    feed = feed.uploadImage
                ).collect { dataState ->
                    when (dataState) {
                        is DataState.Loading -> {
                            // Send button disabled
                            _uiState.update {
                                it.copy(isSendButtonEnabled = false, reply = "")
                            }
                        }

                        is DataState.Success -> {
                            // Reset reply text field
                            _uiState.update {
                                it.copy(
                                    reply = "",
                                    isSendButtonEnabled = false,
                                    isShowReplyTextField = false
                                )
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
                SnackbarManager.showMessage(e.toSnackbarMessage())
            }
        }
    }

    fun onEmojiSelected(emoji: String, feed: Feed) {
        launchCatching {
            try {
                if (_uiState.value.isNetworkAvailable.not()) {
                    throw Exception("Không có kết nối mạng")
                }
                // show emoji animation
                _uiState.update {
                    it.copy(selectedEmoji = emoji)
                }
                // Call API to react feed
                userService.addEmojiReaction(
                    feed.uploadImage.imageId,
                    emoji
                ).collect { dataState ->
                    when (dataState) {
                        is DataState.Loading -> {
                            // Do nothing
                        }

                        is DataState.Success -> {
                            // Reset emoji animation
                            _uiState.update {
                                it.copy(selectedEmoji = "")
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
                SnackbarManager.showMessage(e.toSnackbarMessage())
            }
        }
    }

    fun onSearchEmoji(searchText: String) {
        _uiState.update {
            it.copy(searchText = searchText)
        }
    }

    fun onShowEmojiPicker(showEmojiPicker: Boolean) {
        _uiState.update {
            it.copy(isEmojiPickerVisible = showEmojiPicker)
        }
    }

    fun onShowRelyFeedTextField(showRelyFeedTextField: Boolean) {
        _uiState.update {
            it.copy(isShowReplyTextField = showRelyFeedTextField)
        }
    }

    fun onShowReactionList(showReactionList: Boolean) {
        _uiState.update {
            it.copy(isShowReactionList = showReactionList)
        }
    }

}
