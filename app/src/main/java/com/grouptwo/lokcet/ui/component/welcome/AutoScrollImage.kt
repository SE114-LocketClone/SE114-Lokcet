package com.grouptwo.lokcet.ui.component.welcome

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt


@Composable
fun AutoScrollImage(images: List<Int>, duration: Long) {
    val imageIndex = remember { mutableStateOf(0) }

    LaunchedEffect(key1 = true) {
        while(true) {
            delay(duration) // change image every 'duration' milliseconds
            imageIndex.value = (imageIndex.value + 1) % images.size
        }
    }

    val currentImage by animateIntAsState(
        targetValue = imageIndex.value,
        animationSpec = tween(
            durationMillis = 500,
            easing = LinearEasing
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
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally(targetOffsetX = { -it }),
            content = {
                // Make sure the image always fill the box
                Image(painterResource(id = images[currentImage]), contentDescription = null, modifier = Modifier.fillMaxSize())
            }
        )
    }
}

