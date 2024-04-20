package com.grouptwo.lokcet.view.home

import android.graphics.Bitmap
import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalLensFacing

data class HomeUiState (
    val lensFacing: Int = CameraSelector.LENS_FACING_BACK,
    val capturedImage: Bitmap? = null,
)