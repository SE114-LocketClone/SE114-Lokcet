package com.grouptwo.lokcet.view.error

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grouptwo.lokcet.R
import com.grouptwo.lokcet.ui.theme.BlackSecondary
import com.grouptwo.lokcet.ui.theme.YellowPrimary
import com.grouptwo.lokcet.ui.theme.fontFamily

@Composable
fun ErrorScreen(
    errorMessage: String, onRetry: () -> Unit
) {
    // Display the error screen
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.1f))
            Text(
                text = "Oops! Something went wrong",
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold
                )
            )
            Image(
                painter = painterResource(id = R.drawable.error_image), // Replace this with your own error image
                contentDescription = "Error Image", modifier = Modifier.weight(0.35f)
            )
            Spacer(modifier = Modifier.weight(0.1f))
            Text(
                text = errorMessage, style = TextStyle(
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = fontFamily
                )
            )
            Spacer(modifier = Modifier.weight(0.1f))
            Button(
                onClick = { onRetry() },
                colors = ButtonDefaults.buttonColors(YellowPrimary),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50.dp)
            ) {
                Text(
                    text = "Thử lại", style = TextStyle(
                        fontFamily = fontFamily, textAlign = TextAlign.Center,
                        color = BlackSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                    )
                )
            }
        }
    }
}