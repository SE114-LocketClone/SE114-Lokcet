package com.grouptwo.lokcet.view.home

import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.CameraSelector
import com.grouptwo.lokcet.navigation.Screen
import com.grouptwo.lokcet.view.LokcetViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
) : LokcetViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())

    // Read only access to the uiState
    val uiState: StateFlow<HomeUiState> = _uiState

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

    fun onImageCaptured(imageCapture: Bitmap) {
        // Update the captured image uri
        _uiState.update {
            it.copy(capturedImage = imageCapture)
        }
        Log.d("Image Captured", imageCapture.toString())
    }


    // ViewModel for the HomeScreen
    fun onSwipeUp(navigate: (String) -> Unit) {
        // Navigate to the FeedScreen
        navigate(Screen.FeedScreen.route)
    }
}