package com.grouptwo.lokcet.view.feed

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.grouptwo.lokcet.ui.theme.fontFamily

@Composable
fun FeedScreen() {
    // Display the feed screen
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Display the feed screen
        Text(
            text = "Feed Screen",
            style = TextStyle(
                fontFamily = fontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
        )
    }
}