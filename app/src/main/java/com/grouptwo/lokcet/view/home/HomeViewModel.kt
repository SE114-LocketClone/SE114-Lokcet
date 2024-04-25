package com.grouptwo.lokcet.view.home

import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.lifecycle.viewModelScope
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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val storageService: StorageService,
    private val internetService: InternetService,
    private val userService: UserService
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
        Log.d("Camera Switched", _uiState.value.lensFacing.toString())
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
                if(existing.contains(viewerId)) {
                    // Remove the user id from the list
                    it.copy(visibleToUserIds = existing - viewerId)
                } else {
                    // Add the user id to the list
                    it.copy(visibleToUserIds = existing + viewerId)
                }
            }
        } else {
            _uiState.update {
                it.copy(visibleToUserIds = null)
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

    // ViewModel for the HomeScreen
    fun onSwipeUp(navigate: (String) -> Unit) {
        // Navigate to the FeedScreen
        navigate(Screen.FeedScreen.route)
    }
}