package com.grouptwo.lokcet.ui.component.global

import androidx.annotation.StringRes
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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