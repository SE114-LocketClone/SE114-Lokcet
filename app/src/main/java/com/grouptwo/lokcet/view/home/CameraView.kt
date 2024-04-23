package com.grouptwo.lokcet.view.home

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraInfoUnavailableException
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.MeteringPointFactory
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.grouptwo.lokcet.R
import com.grouptwo.lokcet.ui.component.global.snackbar.SnackbarManager
import com.grouptwo.lokcet.ui.component.global.snackbar.SnackbarMessage.Companion.toSnackbarMessage
import com.grouptwo.lokcet.utils.afterMeasured
import com.grouptwo.lokcet.utils.noRippleClickable
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import java.time.LocalTime

@SuppressLint("ClickableViewAccessibility")
@Composable
fun CameraView(
    modifier: Modifier = Modifier,
    lensFacing: Int,
    onImageCapture: (Bitmap) -> Unit,
    onSwitchCamera: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val (focusPoint, setFocusPoint) = remember { mutableStateOf(Offset.Zero) }
    val (focusRadius, setFocusRadius) = remember { mutableStateOf(0f) }
    val coroutineScope = rememberCoroutineScope()
    val zoomAnimator = ValueAnimator().apply {
        interpolator = LinearInterpolator()
        duration = 300 // The duration of the zoom effect
    }

    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val cameraProvider: ProcessCameraProvider by rememberUpdatedState(newValue = cameraProviderFuture.get())
    var cameraControl: CameraControl? = null
    var camera: Camera? = null
    val preview = remember { Preview.Builder().setTargetAspectRatio(AspectRatio.RATIO_4_3).build() }
    val imageCapture =
        remember { ImageCapture.Builder().setTargetAspectRatio(AspectRatio.RATIO_4_3).build() }
    // Add ScaleGestureDetector here
    val scaleGestureDetector = remember {
        ScaleGestureDetector(context, object : ScaleGestureDetector.OnScaleGestureListener {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val zoomRatio = camera?.cameraInfo?.zoomState?.value?.zoomRatio ?: 0f
                val scale = detector.scaleFactor
                cameraControl?.setZoomRatio(zoomRatio * scale)
                return true
            }

            override fun onScaleBegin(detector: ScaleGestureDetector) = true

            override fun onScaleEnd(detector: ScaleGestureDetector) {}
        })
    }
    LaunchedEffect(lensFacing) {
        // Check if camera is bound then unbind all use cases
        cameraProvider.unbindAll()
        camera = cameraProvider.bindToLifecycle(
            lifecycleOwner,
            if (lensFacing == CameraSelector.LENS_FACING_BACK) CameraSelector.DEFAULT_BACK_CAMERA
            else CameraSelector.DEFAULT_FRONT_CAMERA,
            preview,
            imageCapture
        )
        cameraControl = camera!!.cameraControl

        val flashEnabled =
            LocalTime.now().hour !in 6..18 && lensFacing != CameraSelector.LENS_FACING_FRONT
        cameraControl!!.enableTorch(flashEnabled)
    }

    fun captureImage() {
        val imageCaptured = imageCapture ?: return
        imageCaptured.takePicture(ContextCompat.getMainExecutor(context),
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
            })
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
                .requiredHeight(385.dp)
        ) {
            AndroidView(modifier = Modifier.fillMaxSize(), factory = { context ->
                PreviewView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    setBackgroundColor(android.graphics.Color.BLACK)
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    scaleType = PreviewView.ScaleType.FILL_CENTER

                }.also { previewView ->
                    previewView.setOnTouchListener { _, event ->

                        return@setOnTouchListener true
                    }
                    // Create a listener to set the camera tap to focus
                    previewView.afterMeasured {
                        previewView.setOnTouchListener { _, event ->
                            scaleGestureDetector.onTouchEvent(event)
                            when (event.action) {
                                MotionEvent.ACTION_DOWN -> {
                                    // After the user tap the preview, show the focus indicator by get the focus point
                                    setFocusPoint(Offset(event.x, event.y))
                                    setFocusRadius(100f) // Initial radius of the circle
                                }

                                MotionEvent.ACTION_UP -> {
                                    val factory: MeteringPointFactory =
                                        SurfaceOrientedMeteringPointFactory(
                                            previewView.width.toFloat(),
                                            previewView.height.toFloat()
                                        )
                                    val autoFocusPoint = factory.createPoint(event.x, event.y)
                                    try {
                                        cameraControl?.startFocusAndMetering(
                                            FocusMeteringAction.Builder(
                                                autoFocusPoint,
                                                FocusMeteringAction.FLAG_AF
                                            ).apply {
                                                //focus only when the user tap the preview
                                                disableAutoCancel()
                                            }.build()
                                        )
                                    } catch (e: CameraInfoUnavailableException) {
                                        SnackbarManager.showMessage(e.toSnackbarMessage())
                                    }
                                    // Start the animation for the circle to disappear
                                    coroutineScope.launch {
                                        animate(
                                            initialValue = 100f,
                                            targetValue = 0f,
                                            animationSpec = tween(500, easing = LinearEasing)
                                        ) { value, _ ->
                                            setFocusRadius(value)
                                        }
                                    }
                                }

                                else -> return@setOnTouchListener false // Unhandled event.
                            }
                            return@setOnTouchListener true
                        }
                    }
                    preview.setSurfaceProvider(previewView.surfaceProvider)
                }
            })
            Canvas(modifier = Modifier.matchParentSize()) {
                drawCircle(Color.White, focusRadius, focusPoint, style = Stroke(3f))
            }
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