package com.grouptwo.lokcet.ui.component.global.composable

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grouptwo.lokcet.ui.theme.YellowPrimary
import com.grouptwo.lokcet.ui.theme.fontFamily

@SuppressLint("ModifierFactoryUnreferencedReceiver")
@Composable
fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier {
    return composed {
        clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        )
    }
}

@Composable
fun BasicTextButton(
    @StringRes stringResource: Int,
    modifier: Modifier,
    action: () -> Unit,
    textStyle: TextStyle,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        YellowPrimary
    )
) {
    Button(
        onClick = action, colors = colors, modifier = Modifier
    ) {
        Text(
            text = stringResource(stringResource),
            style = textStyle,
        )
    }
}

@Composable
fun BasicIconButton(
    @DrawableRes drawableResource: Int,
    modifier: Modifier,
    action: () -> Unit,
    description: String,
    colors: Color = YellowPrimary,
    tint: Color = Color.White
) {
    FloatingActionButton(
        onClick = { action() }, shape = CircleShape, containerColor = colors, modifier = modifier
    ) {
        Icon(
            painter = painterResource(id = drawableResource),
            contentDescription = description,
            tint = tint,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun BasicShareButton(
    @DrawableRes drawableResource: Int,
    action: () -> Unit,
    description: String,
    text: String,
) {

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .noRippleClickable { action() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = drawableResource),
            contentDescription = description,
            modifier = Modifier.size(36.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = text, style = TextStyle(
                fontSize = 14.sp,
                fontFamily = fontFamily,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFEDEDED),
                textAlign = TextAlign.Center,
            )
        )
    }
}