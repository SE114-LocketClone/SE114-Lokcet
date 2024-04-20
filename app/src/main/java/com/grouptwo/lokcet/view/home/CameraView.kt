package com.grouptwo.lokcet.view.home

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.grouptwo.lokcet.R
import com.grouptwo.lokcet.ui.component.global.snackbar.SnackbarManager
import com.grouptwo.lokcet.ui.component.global.snackbar.SnackbarMessage.Companion.toSnackbarMessage
import com.grouptwo.lokcet.utils.noRippleClickable
import java.nio.ByteBuffer
import java.time.LocalTime

@Composable
fun CameraView(
    modifier: Modifier = Modifier,
    lensFacing: Int,
    onImageCapture: (Bitmap) -> Unit,
    onSwitchCamera: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val cameraProvider: ProcessCameraProvider by rememberUpdatedState(newValue = cameraProviderFuture.get())

    val preview = remember { Preview.Builder().build() }
    val imageCapture = remember { ImageCapture.Builder().build() }
    LaunchedEffect(lensFacing) {
        // Check if camera is bound then unbind all use cases
        cameraProvider.unbindAll()

        val cameraControl: CameraControl = cameraProvider.bindToLifecycle(
            lifecycleOwner,
            if (lensFacing == CameraSelector.LENS_FACING_BACK) CameraSelector.DEFAULT_BACK_CAMERA
            else CameraSelector.DEFAULT_FRONT_CAMERA,
            preview,
            imageCapture
        ).cameraControl

        val flashEnabled =
            LocalTime.now().hour !in 6..18 && lensFacing != CameraSelector.LENS_FACING_FRONT
        cameraControl.enableTorch(flashEnabled)
    }

    fun captureImage() {
        val imageCaptured = imageCapture ?: return
        imageCaptured.takePicture(
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    val buffer: ByteBuffer = image.planes[0].buffer
                    val bytes = ByteArray(buffer.remaining())
                    buffer.get(bytes)
                    val bitmapImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    onImageCapture(bitmapImage)
                    image.close()
                }

                override fun onError(e: ImageCaptureException) {
                    SnackbarManager.showMessage(e.toSnackbarMessage())
                }
            }
        )
    }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .weight(0.65f)
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(20)
                )
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    PreviewView(context).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        setBackgroundColor(android.graphics.Color.BLACK)
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                        scaleType = PreviewView.ScaleType.FILL_START
                    }.also { previewView ->
                        preview.setSurfaceProvider(previewView.surfaceProvider)
                    }
                }
            )
        }
        Spacer(modifier = Modifier.weight(0.05f))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(painter = painterResource(id = R.drawable.upload_picture),
                contentDescription = "Upload Picture",
                modifier = Modifier
                    .size(50.dp)
                    .noRippleClickable {
                        // Open Image Picker
                    })
            Image(painter = painterResource(id = R.drawable.take_picture),
                contentDescription = "Take Picture",
                modifier = Modifier
                    .size(75.dp)
                    .noRippleClickable {
                        // Take a picture of current viewBox
                        captureImage()

                    })
            Image(painter = painterResource(id = R.drawable.switch_camera),
                contentDescription = "Switch Camera",
                modifier = Modifier
                    .size(50.dp)
                    .noRippleClickable {
                        // Switch camera to front or back
                        onSwitchCamera()
                    })
        }
    }

}