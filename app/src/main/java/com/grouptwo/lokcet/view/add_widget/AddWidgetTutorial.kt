package com.grouptwo.lokcet.view.add_widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
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
import com.grouptwo.lokcet.data.model.WidgetItem
import com.grouptwo.lokcet.ui.theme.GraySecondary
import com.grouptwo.lokcet.ui.theme.fontFamily

@Composable
fun AddWidgetTutorial(item: WidgetItem) {
    // Hold scroll state
    val scrollState = rememberScrollableState { delta ->
        delta / 2 // Adjust this based on how much you want the scroll to move per swipe
    }

    // Display the add_widget item
    Column(
        modifier = Modifier
            .fillMaxSize()
            .scrollable(state = scrollState, orientation = Orientation.Horizontal),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = item.image),
            contentDescription = item.title,
            modifier = Modifier
                .width(150.dp)
                .height(250.dp)
                .fillMaxSize()
        )
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = item.title, style = TextStyle(
                fontSize = 20.sp,
                fontFamily = fontFamily,
                fontWeight = FontWeight.Bold,
                color = GraySecondary,
                textAlign = TextAlign.Center,
            )
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = item.description, style = TextStyle(
                fontSize = 20.sp,
                fontFamily = fontFamily,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
            )
        )
    }
}