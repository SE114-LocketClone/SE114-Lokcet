package com.grouptwo.lokcet.view.feed

import android.content.ContentResolver
import android.content.ContentValues
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.grouptwo.lokcet.R
import com.grouptwo.lokcet.data.model.Feed
import com.grouptwo.lokcet.data.model.User
import com.grouptwo.lokcet.di.paging.FeedRepository
import com.grouptwo.lokcet.di.service.AccountService
import com.grouptwo.lokcet.di.service.ChatService
import com.grouptwo.lokcet.di.service.InternetService
import com.grouptwo.lokcet.di.service.StorageService
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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
@OptIn(ExperimentalPagingApi::class)
class FeedViewModel @Inject constructor(
    private val internetService: InternetService,
    private val userService: UserService,
    private val accountService: AccountService,
    private val feedRepository: FeedRepository,
    private val chatService: ChatService,
    private val sharedPreferences: SharedPreferences,
    private val storageService: StorageService,
    private val contentResolver: ContentResolver
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

    override fun onCleared() {
        super.onCleared()
        // Save the current server time to shared preferences when view model is cleared as a point
        // of reference for the next time the app is opened to retrieve how many feeds have been posted
        // since the last time the app was opened

        // Convert the current server time to a long value and save it to shared preferences
        val currentServerTime = _uiState.value.currentServerTime?.seconds ?: 0
        sharedPreferences.edit().putLong("currentServerTime", currentServerTime).apply()
    }

    fun onSwipeBack(clearAndNavigate: (String) -> Unit) {
        clearAndNavigate(Screen.HomeScreen_1.route)
    }

    fun onShowOptionMenu(showOptionMenu: Boolean) {
        _uiState.update {
            it.copy(showOptionMenu = showOptionMenu)
        }
    }

    fun downloadImage(feed: Feed) {
        launchCatching {
            try {
                if (_uiState.value.isNetworkAvailable.not()) {
                    throw Exception("Không có kết nối mạng")
                }
                // Show progress bar
                _uiState.update {
                    it.copy(showOptionMenu = false)
                }
                // Download image and save to gallery
                val bitmap = storageService.downloadImage(feed.uploadImage.imageUrl)
                // Save image to gallery
                bitmap.let {
                    // File name is IMG_yyyyMMdd_HHmmss.jpg
                    val fileName = "IMG_${System.currentTimeMillis()}.jpg"
                    val values = ContentValues().apply {
                        put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                        // Check android version
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {  // If Android 10 or higher
                            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                        }
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        val uri =
                            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                        uri?.let {
                            val outputStream = contentResolver.openOutputStream(uri)
                            outputStream?.let {
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                                outputStream.close()
                            }
                        }
                    } else { // If Android 9 or lower
                        val imagesDir =
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                                .toString()
                        val imageFile = File(imagesDir, fileName)
                        val fos = FileOutputStream(imageFile)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                        fos.close()
                    }
                }
                SnackbarManager.showMessage(R.string.save_image_success)
            } catch (e: CancellationException) {
                // Do nothing
            } catch (e: Exception) {
                SnackbarManager.showMessage(e.toSnackbarMessage())
            }
        }
    }
}
