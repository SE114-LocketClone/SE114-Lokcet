package com.grouptwo.lokcet.ui.component.global.composable

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import com.grouptwo.lokcet.ui.theme.YellowPrimary

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
        onClick = action,
        colors = colors,
        modifier = Modifier
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
        onClick = { action() },
        shape = CircleShape,
        containerColor = colors,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(id = drawableResource),
            contentDescription = description,
            tint = tint,
            modifier = Modifier.fillMaxSize()
        )
    }
}