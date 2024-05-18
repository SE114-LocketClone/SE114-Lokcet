package com.grouptwo.lokcet.ui.component.welcome

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay


@Composable
fun AutoScrollImage(images: List<Int>, duration: Long, animationDelay: Int = 500) {
    val imageIndex = remember { mutableStateOf(0) }

    LaunchedEffect(key1 = true) {
        while (true) {
            delay(duration) // change image every 'duration' milliseconds
            imageIndex.value = (imageIndex.value + 1) % images.size
            delay(animationDelay.toLong()) // delay before the next image comes in

        }
    }

    val currentImage by animateIntAsState(
        targetValue = imageIndex.value,
        animationSpec = tween(
            durationMillis = animationDelay,
            easing = FastOutSlowInEasing
        )
    )

    Box(
        modifier = Modifier
            .height(362.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = true,
            enter = slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(animationDelay, easing = FastOutSlowInEasing)
            ),
            exit = slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(animationDelay, easing = FastOutSlowInEasing)
            ),
            content = {
                // Make sure the image always fill the box
                Image(
                    painterResource(id = images[currentImage]),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
        )

    }
}

