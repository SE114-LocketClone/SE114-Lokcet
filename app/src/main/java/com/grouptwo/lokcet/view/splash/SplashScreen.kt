package com.grouptwo.lokcet.view.splash

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.grouptwo.lokcet.R
import com.grouptwo.lokcet.utils.Constants
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun SplashScreen(
    openAndPopUp: (String, String) -> Unit,
    viewModel: SplashViewModel = hiltViewModel(),
    dispatcher: CoroutineDispatcher = Dispatchers.Main
) {
    val scale = remember {
        Animatable(0f)
    }
    val overshootInterpolator = remember {
        OvershootInterpolator(2f)
    }
    LaunchedEffect(key1 = true) {
        withContext(dispatcher) {
            scale.animateTo(
                targetValue = 0.5f,
                animationSpec = tween(
                    durationMillis = 500,
                    easing = {
                        overshootInterpolator.getInterpolation(it)
                    }
                )
            )

            // Use delay to wait for the animation to finish
            delay(Constants.SPLASH_SCREEN_DURATION)
            // While the animation is running, the SplashViewModel will determine the next screen to navigate to
            viewModel.onAppStart(openAndPopUp)
        }
    }
    // Define the SplashScreen layout
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(id = R.drawable.app_logo), contentDescription = "Lokcet Logo",
            modifier = Modifier.size(300.dp),
        )
    }
}