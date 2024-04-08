package com.grouptwo.lokcet.view.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grouptwo.lokcet.ui.component.global.composable.BasicTextButton
import com.grouptwo.lokcet.ui.component.welcome.AutoScrollImage
import com.grouptwo.lokcet.ui.theme.BlackSecondary
import com.grouptwo.lokcet.ui.theme.fontFamily
import com.grouptwo.lokcet.R.string as WelcomeString

@Composable
fun WelcomeScreen() {
    // Welcome Screen
    // Display the welcome screen
    val images = listOf(
        com.grouptwo.lokcet.R.drawable.miniphone_1,
        com.grouptwo.lokcet.R.drawable.miniphone_2,
    )
    // Fill the screen with the welcome screen content
    Box(modifier = Modifier.fillMaxSize()) {
        // Display the welcome
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 50.dp, bottom = 15.dp)
        ) {

            // Auto Scroll Image
            AutoScrollImage(images = images, duration = 3000L)

            // Logo Icon and Logo Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 60.dp, bottom = 16.dp)
            ) {
                Image(
                    painter = painterResource(id = com.grouptwo.lokcet.R.drawable.app_logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(50.dp)
                )
                Text(
                    text = stringResource(id = WelcomeString.app_name), style = TextStyle(
                        color = androidx.compose.ui.graphics.Color.White,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = fontFamily
                    ), modifier = Modifier.padding(start = 8.dp)
                )
            }
            Text(
                text = stringResource(id = WelcomeString.welcome_text),
                style = TextStyle(
                    fontSize = 20.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB8B8B8),
                    textAlign = TextAlign.Center,
                ),
                maxLines = 2,
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 32.dp)
                    .fillMaxWidth()
            )

            // Create Account Button
            BasicTextButton(
                stringResource = WelcomeString.create_account,
                modifier = Modifier
                    .width(300.dp)
                    .padding(8.dp)
                    .clip(
                        shape = RoundedCornerShape(50)
                    ),
                action = { /*TODO*/ },
                textStyle = TextStyle(
                    fontSize = 20.sp,
                    fontFamily = fontFamily,
                    color = BlackSecondary,
                    fontWeight = FontWeight.Bold
                ),
            )

            // Login Touchable
            Surface(color = Color.Transparent,
                modifier = Modifier
                    .clickable { /*TODO*/ }
                    .padding(top = 20.dp)
                    .fillMaxWidth()) {
                Text(
                    text = stringResource(id = WelcomeString.login), style = TextStyle(
                        fontSize = 20.sp,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8D8D8D),
                        textAlign = TextAlign.Center,
                    )
                )
            }
        }
    }
}
