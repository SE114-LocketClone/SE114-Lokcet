package com.grouptwo.lokcet.view.home

import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.camera.core.CameraSelector
import androidx.lifecycle.viewModelScope
import com.grouptwo.lokcet.R
import com.grouptwo.lokcet.di.service.AccountService
import com.grouptwo.lokcet.di.service.InternetService
import com.grouptwo.lokcet.di.service.StorageService
import com.grouptwo.lokcet.di.service.UserService
import com.grouptwo.lokcet.navigation.Screen
import com.grouptwo.lokcet.ui.component.global.snackbar.SnackbarManager
import com.grouptwo.lokcet.ui.component.global.snackbar.SnackbarMessage.Companion.toSnackbarMessage
import com.grouptwo.lokcet.utils.ConnectionState
import com.grouptwo.lokcet.utils.DataState
import com.grouptwo.lokcet.utils.compressToJpeg
import com.grouptwo.lokcet.view.LokcetViewModel
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val storageService: StorageService,
    private val internetService: InternetService,
    private val userService: UserService,
    private val contentResolver: ContentResolver,
    private val cacheDir: File,
    private val accountService: AccountService
) : LokcetViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    private val networkStatus: StateFlow<ConnectionState> = internetService.networkStatus.stateIn(
        scope = viewModelScope,
        initialValue = ConnectionState.Unknown,
        started = WhileSubscribed(500000)
    )

    // Read only access to the uiState
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        launchCatching {
            networkStatus.collect { connectionState ->
                _uiState.update {
                    it.copy(isNetworkAvailable = connectionState == ConnectionState.Available || connectionState == ConnectionState.Unknown)
                }
                // Call to fetch friends list to select viewers for the image
                fetchFriendList()
            }
        }
    }

    fun switchCamera() {
        // Switch the camera lens
        _uiState.update {
            it.copy(
                lensFacing = when (it.lensFacing) {
                    CameraSelector.LENS_FACING_BACK -> CameraSelector.LENS_FACING_FRONT
                    CameraSelector.LENS_FACING_FRONT -> CameraSelector.LENS_FACING_BACK
                    else -> CameraSelector.LENS_FACING_BACK
                }
            )
        }
    }

    fun onImageCaptured(imageCapture: Bitmap, navigate: (String) -> Unit) {
        // Update the captured image uri
        launchCatching {
            val compressImage = imageCapture.compressToJpeg()
            _uiState.update {
                it.copy(capturedImage = imageCapture, compressedImage = compressImage)
            }
            // Check if the captured image and compressed image are not null
            // Means the image has been captured and compressed
            if (_uiState.value.capturedImage != null && _uiState.value.compressedImage != null) {
                // Navigate to the ImagePreviewScreen
                navigate(Screen.HomeScreen_2.route)
            }
        }

    }

    fun onInputCaption(caption: String) {
        // Update the caption
        _uiState.update {
            it.copy(imageCaption = caption)
        }
    }

    fun onUserSettingClick(navigate: (String) -> Unit) {
        // Navigate to the SettingScreen
        navigate(Screen.SettingScreen.route)
    }

    fun onClickToUploadImage(clearAndNavigate: (String) -> Unit) {
        launchCatching {
            try {
                // Check the internet connection
                if (_uiState.value.isNetworkAvailable.not()) {
                    throw Exception("No internet connection")
                }
                _uiState.update {
                    it.copy(isImageUpload = DataState.Loading)
                }
                storageService.uploadImage(
                    _uiState.value.compressedImage!!,
                    _uiState.value.imageCaption,
                    _uiState.value.visibleToUserIds
                ).collect {
                    when (it) {
                        // Show loading state
                        is DataState.Loading -> {
                            _uiState.update {
                                it.copy(isImageUpload = DataState.Loading)
                            }
                        }
                        // Show success state
                        is DataState.Success -> {
                            // Update the uiState
                            _uiState.update {
                                it.copy(isImageUpload = DataState.Success(Unit))
                            }
                            // Delay for 2 second to show the success state then popback and clear the uiState
                            delay(2000)
                            // Clear the uiState
                            clearAndNavigate(Screen.HomeScreen_1.route)
                        }
                        // Show error state
                        is DataState.Error -> {
                            throw it.exception
                        }
                    }
                }
            } catch (e: Exception) {
                // Show snackbar with throwable message if there is an exception for UX
                SnackbarManager.showMessage(e.toSnackbarMessage())
            }
        }
    }

    fun getNumOfNewFeeds() {
        launchCatching {
            try {
                if (_uiState.value.isNetworkAvailable.not()) {
                    throw Exception("Không có kết nối mạng")
                }
                val friendList = _uiState.value.friendList
                if (friendList is DataState.Success) {
                    userService.getNumOfNewFeeds(friendList.data.map { it.id } ?: emptyList())
                        .collect { dataState ->
                            when (dataState) {
                                is DataState.Loading -> {
                                    _uiState.update {
                                        it.copy(numOfNewFeeds = 0)
                                    }
                                }

                                is DataState.Success -> {
                                    _uiState.update {
                                        it.copy(numOfNewFeeds = dataState.data)
                                    }
                                }

                                is DataState.Error -> {
                                    throw dataState.exception
                                }
                            }
                        }
                }
            } catch (
                e: CancellationException
            ) {
                // Do nothing only make sure not show the "Job was cancelled" notification
            } catch (e: Exception) { // Show snackbar with throwable message if there is an exception for UX
                SnackbarManager.showMessage(e.toSnackbarMessage())
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
                                it.copy(friendList = DataState.Loading)
                            }
                        }

                        is DataState.Success -> {
                            _uiState.update {
                                it.copy(friendList = DataState.Success(dataState.data))
                            }
                            // Get the number of new feeds
                            getNumOfNewFeeds()
                        }

                        is DataState.Error -> {
                            throw dataState.exception
                        }
                    }
                }

            } catch (e: CancellationException) {
                // Do nothing only make sure not show the "Job was cancelled" notification
            } catch (e: Exception) {
                // Show snackbar with throwable message if there is an exception for UX
                _uiState.update {
                    it.copy(friendList = DataState.Error(e))
                }
                SnackbarManager.showMessage(e.toSnackbarMessage())
            }
        }
    }

    fun onSelectViewer(viewerId: String?) {
        // Update the selected viewers (Default is null means visible to all)
        // Update the list of selected viewers
        if (viewerId != null) {
            _uiState.update {
                val existing = it.visibleToUserIds ?: emptyList()
                // Check if user id exist in list
                if (existing.contains(viewerId)) {
                    // Remove the user id from the list
                    it.copy(visibleToUserIds = existing - viewerId)
                } else {
                    // Add the user id to the list
                    it.copy(visibleToUserIds = existing + viewerId)
                }
            }
        } else {
            _uiState.update {
                it.copy(visibleToUserIds = emptyList())
            }
        }
    }

    fun onClearImage(clearAndNavigate: (String) -> Unit) {
        // Clear the captured image and compressed image
        // Navigate back to the HomeScreen
        clearAndNavigate(Screen.HomeScreen_1.route)
        _uiState.update {
            it.copy(capturedImage = null, compressedImage = null, imageCaption = "")
        }
    }

    fun onSaveImage(imageCapture: Bitmap?) {
        // Save the image to the gallery
        imageCapture?.let {
            launchCatching {
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
                            imageCapture.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                            outputStream.close()
                        }
                    }
                } else { // If Android 9 or lower
                    val imagesDir =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                            .toString()
                    val imageFile = File(imagesDir, fileName)
                    val fos = FileOutputStream(imageFile)
                    imageCapture.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                    fos.close()
                }
                // If the image is saved successfully
                _uiState.update {
                    it.copy(savedImageSuccess = true)
                }
                SnackbarManager.showMessage(R.string.save_image_success)
            }
        }
    }

    fun onImagePicked(uri: Uri?, navigate: (String) -> Unit) {
        launchCatching {
            // Update the image uri
            _uiState.update {
                val bitmap = uri?.let {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        val source = ImageDecoder.createSource(contentResolver, uri)
                        ImageDecoder.decodeBitmap(source)
                    } else {
                        @Suppress("DEPRECATION") MediaStore.Images.Media.getBitmap(
                            contentResolver,
                            uri
                        )
                    }
                }
                it.copy(imagePicked = uri, capturedImage = bitmap)
            }
            // Check if captured image is not null
            if (_uiState.value.capturedImage != null) {
                // Navigate to the ImagePreviewScreen
                onImageCaptured(_uiState.value.capturedImage!!, navigate)
            }
        }
    }

    // ViewModel for the HomeScreen
    fun onSwipeUp(navigate: (String) -> Unit) {
        // Set the number of new feeds to 0 because the user has seen the new feeds
        _uiState.update {
            it.copy(numOfNewFeeds = 0)
        }
        // Navigate to the FeedScreen
        navigate(Screen.FeedScreen.route)
    }

    fun onFriendButtonClick(navigate: (String) -> Unit) {
        // Navigate to the FriendScreen
        navigate(Screen.FriendScreen.route)
    }

    fun onChatButtonClick(navigate: (String) -> Unit) {
        // Navigate to the ChatScreen
        navigate(Screen.ChatScreen_1.route)
    }

    fun startCropping(
        activity: Activity, sourceUri: Uri, cropResultLauncher: ActivityResultLauncher<Intent>
    ) {
        val destinationUri = Uri.fromFile(File(activity.cacheDir, "cropped"))
        val intent = UCrop.of(sourceUri, destinationUri).withAspectRatio(1f, 1f).getIntent(activity)
        cropResultLauncher.launch(intent)
    }

    fun onShowAddFriendDialog(showAddFriendDialog: Boolean) {
        // Show the AddFriendDialog
        _uiState.update {
            it.copy(isShowAddFriendDialog = showAddFriendDialog)
        }
    }

    fun getNameFromUid(uid: String) {
        launchCatching {
            try {
                if (_uiState.value.isNetworkAvailable.not()) {
                    throw Exception("Không có kết nối mạng")
                }
                userService.getUserNameFromId(uid).collect { dataState ->
                    when (dataState) {
                        is DataState.Loading -> {
                            _uiState.update {
                                it.copy(friendName = "")
                            }
                        }

                        is DataState.Success -> {
                            _uiState.update {
                                it.copy(friendName = dataState.data)
                            }
                        }

                        is DataState.Error -> {
                            throw dataState.exception
                        }
                    }
                }
            } catch (e: CancellationException) {
            } catch (e: Exception) {
                SnackbarManager.showMessage(e.toSnackbarMessage())
            }
        }
    }

    fun onAddFriendClick(friendId: String) {
        launchCatching {
            try {
                if (_uiState.value.isNetworkAvailable.not()) {
                    throw Exception("Không có kết nối mạng")
                }
                val currentUser = accountService.getCurrentUser()
                // Check if the friendId is the same as the userId
                if (currentUser.id == friendId) {
                    throw Exception("Không thể thêm chính mình làm bạn")
                }
                // Check if the friendId is already in the friend list
                if (friendId in currentUser.friends) {
                    throw Exception("Người dùng đã nằm trong danh sách bạn bè")
                }
                userService.addFriend(currentUser.id, friendId).collect { dataState ->
                    when (dataState) {
                        is DataState.Loading -> {
                            // Do nothing
                        }

                        is DataState.Success -> {
                            // Display a snackbar message
                            SnackbarManager.showMessage(R.string.add_friend_success)
                            _uiState.update {
                                it.copy(isShowAddFriendDialog = false)
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
                    it.copy(isShowAddFriendDialog = false)
                }
                SnackbarManager.showMessage(e.toSnackbarMessage())
            }
        }
    }

    fun onClearUid() {
        _uiState.update {
            it.copy(friendName = "")
        }
    }
}