package com.grouptwo.lokcet.view.chat

import androidx.lifecycle.viewModelScope
import com.grouptwo.lokcet.data.model.UploadImage
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
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatService: ChatService,
    private val accountService: AccountService,
    private val userService: UserService,
    private val internetService: InternetService
) : LokcetViewModel() {
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()
    private val networkStatus = internetService.networkStatus.stateIn(
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
                fetchCurrentServerTime()
                // When the state changes then try to fetch the friend list
                getFriendList()
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
    // get friend list
    fun getFriendList() {
        launchCatching {
            try {
                if (_uiState.value.isNetworkAvailable.not()) {
                    throw Exception("Không có kết nối mạng")
                }
                val currentUser = accountService.getCurrentUser()
                userService.getFriendList().collect { dataState ->
                    when (dataState) {
                        is DataState.Loading -> {
                            _uiState.update {
                                it.copy(friendList = DataState.Loading)
                            }
                        }

                        is DataState.Success -> {
                            val friendMap = dataState.data.associateBy { it.id }
                            _uiState.update {
                                it.copy(
                                    friendList = DataState.Success(dataState.data),
                                    friendMap = friendMap,
                                    currentUser = currentUser
                                )
                            }
                            // Get chat room list
                            getChatRoomList()
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
                    it.copy(friendList = DataState.Error(e))
                }
                SnackbarManager.showMessage(e.toSnackbarMessage())
            }
        }
    }

    // get chat room list
    fun getChatRoomList() {
        launchCatching {
            try {
                if (_uiState.value.isNetworkAvailable.not()) {
                    throw Exception("Không có kết nối mạng")
                }
                val friendList = _uiState.value.friendList
                if (friendList !is DataState.Success) {
                    throw Exception("Không thể lấy danh sách bạn bè")
                }
                chatService.getChatRoomList(
                    friendList = friendList.data
                ).collect { dataState ->
                    when (dataState) {
                        is DataState.Loading -> {
                            _uiState.update {
                                it.copy(chatRoomList = emptyList())
                            }
                        }

                        is DataState.Success -> {
                            _uiState.update {
                                it.copy(chatRoomList = dataState.data)
                            }
                            // get latest message
                            getLatestMessage()
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
                    it.copy(chatRoomList = emptyList())
                }
                SnackbarManager.showMessage(e.toSnackbarMessage())
            }
        }
    }

    // get latest message
    fun getLatestMessage() {
        launchCatching {
            try {
                if (_uiState.value.isNetworkAvailable.not()) {
                    throw Exception("Không có kết nối mạng")
                }
                chatService.getLastestMessage(
                    chatRoomList = _uiState.value.chatRoomList
                ).collect { dataState ->
                    when (dataState) {
                        is DataState.Loading -> {
                            _uiState.update {
                                it.copy(latestMessageMap = emptyMap())
                            }
                        }

                        is DataState.Success -> {
                            _uiState.update {
                                it.copy(latestMessageMap = dataState.data)
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
                    it.copy(latestMessageMap = emptyMap())
                }
                SnackbarManager.showMessage(e.toSnackbarMessage())
            }
        }
    }

    fun onBackClick(
        popUp: () -> Unit
    ) {
        popUp()
    }

    fun onChatItemClick(chatRoomId: String) {
        // Navigate to ChatDetail screen
    }

    // get message list
    fun getMessageList(chatRoomId: String) {
        launchCatching {
            try {
                if (_uiState.value.isNetworkAvailable.not()) {
                    throw Exception("Không có kết nối mạng")
                }
                chatService.getMessageList(
                    chatRoomId = chatRoomId
                ).collect { dataState ->
                    when (dataState) {
                        is DataState.Loading -> {
                            _uiState.update {
                                it.copy(messageList = DataState.Loading)
                            }
                        }

                        is DataState.Success -> {
                            // Do nothing
                            _uiState.update {
                                it.copy(messageList = DataState.Success(dataState.data))
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
                    it.copy(messageList = DataState.Error(e))
                }
                SnackbarManager.showMessage(e.toSnackbarMessage())
            }
        }
    }
    // Get upload image

}