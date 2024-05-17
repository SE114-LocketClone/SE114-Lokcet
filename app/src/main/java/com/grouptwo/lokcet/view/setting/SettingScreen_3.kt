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
fun SettingScreen3(
    popUp: () -> Unit,
    viewModel: SettingViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()
    val imeState = rememberImeState()
    val scrollState = rememberScrollState()

    LaunchedEffect(key1 = imeState.value) {
        if (imeState.value) {
            scrollState.animateScrollTo(scrollState.maxValue, tween(300))
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .padding(start = 16.dp, end = 16.dp)
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
            Spacer(modifier = Modifier.weight(0.1f))
            Text(
                text = "Report a problem",
                style = TextStyle(
                    fontSize = 26.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight(400),
                    color = Color.White,
                )

            )

            val textFieldColors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF272626),
                unfocusedContainerColor = Color(0xFF272626),
                unfocusedIndicatorColor = Color.Black,
                focusedIndicatorColor = Color.Black,
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = uiState.value.email,
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 22.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFFFFF)
                ),
                placeholder = {
                    Text(
                        text = "Your email address", style = TextStyle(
                            color = Color(0xFF737070),
                            fontFamily = fontFamily,
                            fontSize = 18.sp,
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
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = uiState.value.reportProblem,
                onValueChange = {
                    viewModel.onReportProblemChange(it)
                },
                textStyle = TextStyle(
                    fontSize = 15.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFFFFF)
                ),
                placeholder = {
                    Text(
                        text = "Report problem here", style = TextStyle(
                            color = Color(0xFF737070),
                            fontFamily = fontFamily,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                singleLine = false,
                maxLines = 5,
                minLines = 5,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 199.dp).padding(bottom = 16.dp),
                colors = textFieldColors,
                shape = RoundedCornerShape(18.dp),
            )
            Spacer(modifier = Modifier.weight(0.1f))
            val buttonColor = ButtonDefaults.buttonColors(YellowPrimary)
            Button(
                onClick = { viewModel.onReportProblem(popUp) },
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = 46.dp),
                colors = buttonColor
            )
            {
                if (uiState.value.isReportProblemLoading) {
                    // Show loading icon
                    CircularProgressIndicator(
                        color = BlackSecondary, modifier = Modifier.size(40.dp)
                    )
                } else {
                    Text(
                        text = "Gửi", style = TextStyle(
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