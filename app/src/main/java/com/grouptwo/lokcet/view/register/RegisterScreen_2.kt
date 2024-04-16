package com.grouptwo.lokcet.view.register

import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.grouptwo.lokcet.R
import com.grouptwo.lokcet.ui.component.global.composable.BasicIconButton
import com.grouptwo.lokcet.ui.component.global.ime.rememberImeState
import com.grouptwo.lokcet.ui.theme.BlackSecondary
import com.grouptwo.lokcet.ui.theme.YellowPrimary
import com.grouptwo.lokcet.ui.theme.fontFamily


@Composable
fun RegisterScreen2(
    popUp: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState
    val imeState = rememberImeState()
    val scrollState = rememberScrollState()

    LaunchedEffect(key1 = imeState.value) {
        if (imeState.value) {
            scrollState.animateScrollTo(scrollState.maxValue, tween(300))
        }
    }

    val annotatedString = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                fontSize = 15.sp,
                fontFamily = fontFamily,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFB8B8B8)
            )
        )
        {
            append("Mật khẩu của bạn phải dài tối thiểu")
        }
        withStyle(
            style = SpanStyle(
                fontSize = 15.sp,
                fontFamily = fontFamily,
                fontWeight = FontWeight.Bold,
                color = YellowPrimary
            )
        )
        {
            append(" 6 ký tự")
        }
    }

    Box(modifier = Modifier.fillMaxSize())
    {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .padding(start = 16.dp, end = 16.dp)
                .verticalScroll(scrollState)

        )
        {
            BasicIconButton(
                drawableResource = R.drawable.arrow_left,
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.Start),
                action = { viewModel.onBackClick(popUp) },
                description = "Back icon",
                colors = Color(0xFF948F8F),
                tint = Color.White
            )
            Spacer(modifier = Modifier.weight(0.1f))
            Text(
                text = "Chọn một mật khẩu",
                style = TextStyle(
                    fontSize = 26.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight(400),
                    color = Color.White,
                ),
            )

            val textFieldColors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF272626),
                unfocusedContainerColor = Color(0xFF272626),
                unfocusedIndicatorColor = Color.Black,
                focusedIndicatorColor = Color.Black,
            )

            TextField(
                value = uiState.password,
                singleLine = true,
                onValueChange = { viewModel.onPasswordChange(it) },
                textStyle = TextStyle(
                    fontSize = 23.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                placeholder = {
                    Text(
                        text = "Mật khẩu",
                        style = TextStyle(
                            color = Color(0xFF737070),
                            fontFamily = fontFamily,
                            fontSize = 23.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    )
                },
                shape = RoundedCornerShape(18.dp),
                colors = textFieldColors, modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 62.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = annotatedString,
                style = TextStyle.Default.copy(textAlign = TextAlign.Center),
                modifier = Modifier.fillMaxWidth(),
                minLines = 1,
            )
            Spacer(modifier = Modifier.weight(0.1f))
            val buttonColor = ButtonDefaults.buttonColors(
                if (uiState.isButtonPasswordEnable) YellowPrimary else Color(0xFF272626)
            )
            Button(
                onClick = { viewModel.onPasswordClick() },
                modifier = Modifier
                    .width(294.dp)
                    .heightIn(min = 46.dp),
                colors = buttonColor
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Tiếp tục", style = TextStyle(
                            fontSize = 24.sp,
                            fontFamily = fontFamily,
                            color = BlackSecondary,
                            fontWeight = FontWeight.Bold
                        ), modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Image(
                        painter = painterResource(id = R.drawable.arrow_right),
                        contentDescription = "image description",
                        modifier = Modifier
                            .fillMaxHeight()
                            .align(Alignment.CenterVertically)
                    )
                }
            }
        }
    }

}