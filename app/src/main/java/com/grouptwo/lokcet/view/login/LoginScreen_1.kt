package com.grouptwo.lokcet.view.login

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.grouptwo.lokcet.R
import com.grouptwo.lokcet.ui.component.global.composable.BasicIconButton
import com.grouptwo.lokcet.ui.theme.fontFamily

@Composable
fun LoginScreen1(popUp: () -> Unit, viewModel: LoginViewModel = hiltViewModel()){

    //Format for policy string
    val annotatedString = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                fontSize = 11.sp,
                fontFamily = fontFamily,
                fontWeight = FontWeight(400),
                color = Color(0xFFB8B8B8),

                )
        ) {
            append("Thông qua việc chạm vào nút Tiếp tục, bạn đồng ý với các ")
        }
        withStyle(
            style = SpanStyle(
                fontSize = 11.sp,
                fontFamily = fontFamily,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
        ) {
            append("Điều khoản dịch vụ")
        }
        withStyle(
            style = SpanStyle(
                fontSize = 11.sp,
                fontFamily = fontFamily,
                fontWeight = FontWeight(400),
                color = Color(0xFFB8B8B8),

                )
        ) {
            append(" và ")
        }
        withStyle(
            style = SpanStyle(
                fontSize = 11.sp,
                fontFamily = fontFamily,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
        ) {
            append("Chính sách quyền riêng tư")
        }
        withStyle(
            style = SpanStyle(
                fontSize = 11.sp,
                fontFamily = fontFamily,
                fontWeight = FontWeight(400),
                color = Color(0xFFB8B8B8),

                )
        ) {
            append(" của chúng tôi.")
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
                .padding(start = 16.dp, end = 16.dp)
            //.verticalScroll(scrollState)

        ) {
            //--Back button--\\

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
            Spacer(modifier = Modifier
                .padding(0.dp)
                .width(38.dp)
                .height(38.dp))

            //--Email--\\
            Text(
                text = "Email của bạn là gì?",
                style = TextStyle(
                    fontSize = 26.sp,
                    fontFamily = fontFamily,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier
                .width(342.dp)
                .height(79.dp))

            //--Text field for fill email--\\
            val textFieldColors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF272626),
                unfocusedContainerColor = Color(0xFF272626),
                unfocusedIndicatorColor = Color.Black,
                focusedIndicatorColor = Color.Black,
            )

            TextField(
                value =
                onValueChange = { viewModel.onEmailChange(it) },
                singleLine = true,
                )

            //--Policy string--\\
            Text(
                minLines = 2,
                text = annotatedString,
                style = TextStyle.Default.copy(textAlign = TextAlign.Center),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}




