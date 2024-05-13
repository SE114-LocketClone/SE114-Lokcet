package com.grouptwo.lokcet.view.home

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraInfoUnavailableException
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
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
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@SuppressLint("ClickableViewAccessibility")
@Composable
fun CameraView(
    modifier: Modifier = Modifier,
    lensFacing: Int,
    onImageCapture: (Bitmap) -> Unit,
    onSwitchCamera: () -> Unit,
    launcher: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>
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
    val luminosity = remember { mutableStateOf(0f) }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val cameraProvider: ProcessCameraProvider by rememberUpdatedState(newValue = cameraProviderFuture.get())
    var cameraControl: CameraControl? = null
    var camera: Camera? = null
    val preview = remember { Preview.Builder().setTargetAspectRatio(AspectRatio.RATIO_4_3).build() }
    val imageCapture =
        remember {
            ImageCapture.Builder().setTargetAspectRatio(AspectRatio.RATIO_4_3).setFlashMode(
                ImageCapture.FLASH_MODE_AUTO
            ).build()
        }
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
        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        val cameraExecutor = Executors.newSingleThreadExecutor()

        imageAnalysis.setAnalyzer(cameraExecutor, LuminosityAnalyzer { luma ->
            luminosity.value = luma
        })
        camera = cameraProvider.bindToLifecycle(
            lifecycleOwner,
            if (lensFacing == CameraSelector.LENS_FACING_BACK) CameraSelector.DEFAULT_BACK_CAMERA
            else CameraSelector.DEFAULT_FRONT_CAMERA,
            preview,
            imageAnalysis,
            imageCapture
        )
        cameraControl = camera!!.cameraControl
    }

    fun captureImage() {
        val imageCaptured = imageCapture ?: return
        // Check if the camera supports flash
        if (camera?.cameraInfo?.hasFlashUnit() == true) {
            // If the luminosity is less than 0.5, turn on the flash
            if (luminosity.value < 0.5 && lensFacing != CameraSelector.LENS_FACING_FRONT) {
                imageCaptured.flashMode = ImageCapture.FLASH_MODE_ON
            } else {
                imageCaptured.flashMode = ImageCapture.FLASH_MODE_OFF
            }
        }
        imageCaptured.takePicture(ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    val buffer: ByteBuffer = image.planes[0].buffer
                    val bytes = ByteArray(buffer.remaining())
                    buffer.get(bytes)
                    var bitmapImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    val matrix = Matrix()
                    val rotationDegrees = image.imageInfo.rotationDegrees // Get the rotation degree
                    if (lensFacing == CameraSelector.LENS_FACING_FRONT) {
                        matrix.preScale(-1f, 1f)
                        matrix.postRotate(-rotationDegrees.toFloat()) // Rotate the image for front camera
                    } else {
                        matrix.postRotate(rotationDegrees.toFloat()) // Rotate the image for back camera
                    }
                    bitmapImage = Bitmap.createBitmap(
                        bitmapImage,
                        0,
                        0,
                        bitmapImage.width,
                        bitmapImage.height,
                        matrix,
                        true
                    )
                    // Rotate the image
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
                        launcher.launch(
                            PickVisualMediaRequest(
                                mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
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

class LuminosityAnalyzer(private val onFrameAnalyzed: (Float) -> Unit) : ImageAnalysis.Analyzer {
    private var lastAnalyzedTimestamp = 0L

    // This should be a value between 0 and 1. The lower the value, the more likely the flash will be turned on.
    private val luminosityThreshold = 0.5f


    override fun analyze(image: ImageProxy) {
        val currentTimestamp = System.currentTimeMillis()
        if (currentTimestamp - lastAnalyzedTimestamp >= TimeUnit.SECONDS.toMillis(1)) {
            val buffer = image.planes[0].buffer
            val data = ByteArray(buffer.remaining()).apply { buffer.get(this) }
            val pixels = data.map { it.toInt() and 0xFF }
            val luma = pixels.average()

            onFrameAnalyzed((luma / 255.0).toFloat())
            lastAnalyzedTimestamp = currentTimestamp
        }
        image.close()
    }
}