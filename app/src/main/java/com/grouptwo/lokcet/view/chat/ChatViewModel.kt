package com.grouptwo.lokcet.view.chat

import androidx.lifecycle.viewModelScope
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
    val uiState = _uiState.asStateFlow()
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
                // When the state changes then try to fetch the friend list
                getFriendList()
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
                                    friendMap = friendMap
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
                throw e
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
                throw e
            } catch (e: Exception) {
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
                throw e
            } catch (e: Exception) {
                SnackbarManager.showMessage(e.toSnackbarMessage())
            }
        }
    }

    fun onBackClick(
        popUp: () -> Unit
    ) {
        popUp()
    }
}