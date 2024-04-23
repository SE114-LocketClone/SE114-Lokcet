package com.grouptwo.lokcet.view.user

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.grouptwo.lokcet.ui.component.global.ime.rememberImeState
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grouptwo.lokcet.R
import com.grouptwo.lokcet.ui.component.global.composable.BasicIconButton
import com.grouptwo.lokcet.ui.theme.BlackSecondary
import com.grouptwo.lokcet.ui.theme.YellowPrimary
import com.grouptwo.lokcet.ui.theme.fontFamily
@Composable
fun ReportProblemScreen() {
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
            Spacer(modifier = Modifier.weight(0.02f))
            BasicIconButton(
                drawableResource = R.drawable.arrow_left,
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.Start),
                action = { },
                description = "Back icon",
                colors = Color(0xFF948F8F),
                tint = Color.White
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Make a suggestion",
                style = androidx.compose.ui.text.TextStyle(
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
            var email by remember {
                mutableStateOf("")
            }
            Spacer(modifier = Modifier.height(24.dp))
            TextField(value = email,
                onValueChange ={email=it},
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 23.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFFFFF)),
                placeholder = { Text(
                    text = "Your email address", style = TextStyle(
                        color = Color(0xFF737070),
                        fontFamily = fontFamily,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold))},
                modifier= Modifier
                    .fillMaxWidth()
                    .heightIn(min = 62.dp),
                colors = textFieldColors,
                shape = RoundedCornerShape(18.dp),
            )
            Spacer(modifier = Modifier.height(16.dp))
            var suggest by remember {
                mutableStateOf("")
            }
            TextField(value = suggest,
                onValueChange ={suggest=it},
                textStyle = TextStyle(
                    fontSize = 15.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFFFFF)),
                placeholder = { Text(
                    text = "Your suggestion", style = TextStyle(
                        color = Color(0xFF737070),
                        fontFamily = fontFamily,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold))},
                modifier= Modifier
                    .fillMaxWidth()
                    .heightIn(min = 199.dp),
                colors = textFieldColors,
                shape = RoundedCornerShape(18.dp),
            )
            Spacer(modifier = Modifier.weight(0.1f))
            val buttonColor=ButtonDefaults.buttonColors(YellowPrimary)
            Button(onClick = { /*TODO*/ },
                Modifier
                    .width(294.dp)
                    .heightIn(min = 46.dp)
                    .padding(top=10.dp),
                colors = buttonColor)
            {
                Text(text = "Gửi",
                    style = TextStyle(
                        fontSize = 26.sp,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    ),
                )
            }
        }
    }
}