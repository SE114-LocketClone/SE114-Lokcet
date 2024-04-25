package com.grouptwo.lokcet.view.home

import android.graphics.Bitmap
import androidx.camera.core.CameraSelector
import com.grouptwo.lokcet.data.model.User
import com.grouptwo.lokcet.utils.DataState

data class HomeUiState(
    val lensFacing: Int = CameraSelector.LENS_FACING_BACK,
    val capturedImage: Bitmap? = null,
    val compressedImage: ByteArray? = null,
    val imageCaption: String = "",
    val visibleToUserIds: List<String>? = null,
    val isImageUpload: DataState<Unit>?= null,
    val isNetworkAvailable: Boolean = false,
    val friendList: DataState<List<User>> = DataState.Loading,
)