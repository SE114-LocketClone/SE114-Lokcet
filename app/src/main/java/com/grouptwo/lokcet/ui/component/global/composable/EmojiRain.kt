import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

data class Emoji(
    val emoji: String,
    val x: Float,
    val startY: Float,
    val endY: Float,
    val size: Float,
    val duration: Int,
    val opacity: Float
)

private const val START_OF_ANIMATION = 0
private const val TARGET_PARTICLE_SCALE_MULTIPLIER = 1.3f


@Composable
fun EmojiRain(emoji: String) {
    val density = LocalDensity.current.density
    val width = LocalConfiguration.current.screenWidthDp.dp
    val height = LocalConfiguration.current.screenHeightDp.dp
    val emojis = remember { mutableStateListOf<Emoji>() }

    // Initial emoji population
    if (emojis.isEmpty()) {
        repeat(50) {
            emojis.add(
                Emoji(
                    emoji = emoji,
                    x = Random.nextFloat() * width.value,
                    startY = -50f,  // Start above the screen
//                    endY = height.value + 50f,  // End below the screen
                    endY = 1800f,  // End below the screen
                    size = Random.nextFloat() * 50f,
                    duration = Random.nextInt(5000, 10000),
                    opacity = Random.nextFloat()
                )
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        emojis.forEach { emoji ->
            val infiniteTransition = rememberInfiniteTransition()
            val y = infiniteTransition.animateFloat(
                initialValue = emoji.startY,
                targetValue = emoji.endY,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = emoji.duration
                        emoji.startY at START_OF_ANIMATION with LinearEasing
                        emoji.endY at emoji.duration with FastOutSlowInEasing
                    },
                    repeatMode = RepeatMode.Restart
                )
            )
            val opacity = infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 0f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = emoji.duration
                        1f at START_OF_ANIMATION with LinearEasing
                        1f at (emoji.duration * 0.8f).toInt() with LinearEasing
                        0f at emoji.duration with FastOutSlowInEasing
                    },
                    repeatMode = RepeatMode.Restart
                )
            )
            val scale = infiniteTransition.animateFloat(
                initialValue = emoji.size,
                targetValue = emoji.size * TARGET_PARTICLE_SCALE_MULTIPLIER,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = emoji.duration
                        emoji.size at START_OF_ANIMATION with LinearEasing
                        emoji.size at (emoji.duration * 0.7f).toInt() with LinearEasing
                        (emoji.size * TARGET_PARTICLE_SCALE_MULTIPLIER) at emoji.duration with FastOutSlowInEasing
                    },
                    repeatMode = RepeatMode.Restart
                )
            )

            Text(
                text = AnnotatedString(emoji.emoji),
                style = TextStyle(
                    fontSize = scale.value.sp,
                    color = Color.White.copy(alpha = opacity.value)
                ),
                modifier = Modifier.offset(x = emoji.x.dp, y = y.value.dp)
            )
        }
    }
}