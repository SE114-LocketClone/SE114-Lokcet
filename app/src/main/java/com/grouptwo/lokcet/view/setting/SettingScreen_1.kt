package com.grouptwo.lokcet.view.setting

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
fun SettingScreen1(
    viewModel: SettingViewModel = hiltViewModel(),
    popUp: () -> Unit
) {

    val imeState = rememberImeState()
    val scrollState = rememberScrollState()
    val uiState = viewModel.uiState.collectAsState()
    LaunchedEffect(key1 = imeState.value) {
        if (imeState.value) {
            scrollState.animateScrollTo(scrollState.maxValue, tween(300))
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState)
        ) {
            BasicIconButton(
                drawableResource = R.drawable.arrow_left,
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.Start),
                action = {
                    viewModel.onBackClick(popUp)
                },
                description = "Back icon",
                colors = Color(0xFF948F8F),
                tint = Color.White
            )
            Text(
                text = "Sửa tên của bạn",
                style = TextStyle(
                    fontSize = 26.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            )

            val textFieldColors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF272626),
                unfocusedContainerColor = Color(0xFF272626),
                unfocusedIndicatorColor = Color.Black,
                focusedIndicatorColor = Color.Black,
            )
            Spacer(modifier = Modifier.weight(0.1f))
            TextField(
                value = uiState.value.firstNameField,
                onValueChange = {
                    viewModel.onFirstNameChange(it)
                },
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 23.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFFFFF)
                ),
                placeholder = {
                    Text(
                        text = "Tên", style = TextStyle(
                            color = Color(0xFF737070),
                            fontFamily = fontFamily,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 62.dp),
                colors = textFieldColors,
                shape = RoundedCornerShape(18.dp),
            )
            Spacer(modifier = Modifier.height(12.dp))
            TextField(
                value = uiState.value.lastNameField,
                onValueChange = { viewModel.onLastNameChange(it) },
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 23.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFFFFF)
                ),
                placeholder = {
                    Text(
                        text = "Họ", style = TextStyle(
                            color = Color(0xFF737070),
                            fontFamily = fontFamily,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 62.dp),
                colors = textFieldColors,
                shape = RoundedCornerShape(18.dp),
            )
            val buttonColor = ButtonDefaults.buttonColors(YellowPrimary)
            Spacer(modifier = Modifier.weight(0.1f))
            Button(
                onClick = {
                    viewModel.onNameChange()
                },
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = 46.dp),
                colors = buttonColor
            )
            {
                if (uiState.value.isChangeNameLoading) {
                    // Show loading icon
                    CircularProgressIndicator(
                        color = BlackSecondary, modifier = Modifier.size(40.dp)
                    )
                } else {
                    Text(
                        text = "Lưu", style = TextStyle(
                            fontSize = 24.sp,
                            fontFamily = fontFamily,
                            color = BlackSecondary,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }
        }
    }
}