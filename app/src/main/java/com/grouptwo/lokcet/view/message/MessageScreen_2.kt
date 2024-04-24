package com.grouptwo.lokcet.view.message

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.grouptwo.lokcet.R
import com.grouptwo.lokcet.ui.component.global.composable.BasicIconButton
import com.grouptwo.lokcet.ui.theme.BlackSecondary
import com.grouptwo.lokcet.ui.theme.fontFamily

@Composable
fun MessageScreen_2(viewModel : MessageViewModel = hiltViewModel(),
                    popUp:() -> Unit)
{
    Box(modifier = Modifier.fillMaxSize())
    {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            //verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 16.dp)
            //.verticalScroll(scrollState)

        ) {
            Spacer(modifier = Modifier.height(31.dp))
            Row (modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.height(70.dp))
                BasicIconButton(
                    drawableResource = R.drawable.arrow_left,
                    modifier = Modifier
                        .size(31.dp)
                        .align(Alignment.CenterVertically),
                    action = { viewModel.onBackClick(popUp) },
                    description = "Back icon",
                )

                Image(painter = painterResource(id = R.drawable.icon_friend),
                    contentDescription = "avt",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .size(40.dp)
                    )

                Text(
                    text = "abc...",
                    modifier = Modifier.align(Alignment.CenterVertically),
                    style = TextStyle(
                        color = Color.White,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                )
            }
        }
        Row(modifier = Modifier.fillMaxSize())
        {

        }
        //Row(modifier)
    }
}
//@Composable
//fun MessageCard(message: Message) {
//    // Create a Boolean state to track whether the message is expanded or not
//    val isExpanded = remember { mutableStateOf(false) }
//    // Create a color state to change the background color of the message card
//    val surfaceColor: Color by animateColorAsState(
//        // Use a different color when the message is expanded
//        if (isExpanded.value) MaterialTheme.colors.primary else MaterialTheme.colors.surface
//    )
//    // A horizontal card with a 1dp elevation
//    Card(
//        elevation = 1.dp,
//        // Use the color state for the background color
//        backgroundColor = surfaceColor,
//        // Add a click listener to toggle the expanded state
//        modifier = Modifier.clickable { isExpanded.value = !isExpanded.value }
//    ) {
//        // A horizontal row to hold the avatar and the message content
//        Row {
//            // A 40x40 image with 16dp of padding
//            Image(
//                painter = painterResource(id = R.drawable.icon_friend),
//                contentDescription = "Profile picture",
//                modifier = Modifier
//                    .size(40.dp)
//                    .padding(16.dp)
//            )
//            // A vertical column to hold the author name and the message body
//            Column {
//                // A text with the author name in bold and 16sp font size
//                Text(
//                    text = message.author,
//                    fontWeight = FontWeight.Bold,
//                    fontSize = 16.sp
//                )
//                // A text with the message body and 8dp of top padding
//                Text(
//                    text = message.body,
//                    modifier = Modifier.padding(top = 8.dp)
//                )
//                // A crossfade animation to show the timestamp when the message is expanded
//                Crossfade(targetState = isExpanded.value) { expanded ->
//                    if (expanded) {
//                        // A text with the timestamp and 4dp of bottom padding
//                        Text(
//                            text = "2023-04-15 10:35",
//                            fontSize = 12.sp,
//                            modifier = Modifier.padding(bottom = 4.dp)
//                        )
//                    }
//                }
//            }
//        }
//    }
//        // Animate the content size of the card when it is expanded or collapsed
//        .animateContentSize()
//}
@Composable
fun MessageCard(function: () -> Unit) {
    // A horizontal card with a 1dp elevation
    Card(elevation = CardDefaults.cardElevation()) {
        // A horizontal row to hold the avatar and the message content
        Row {
            // A 40x40 image with 16dp of padding
            Image(
                painter = painterResource(id = R.drawable.icon_friend),
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(40.dp)
                    .padding(16.dp) // Add the closing parenthesis here
            )
            // A vertical column to hold the author name and the message body
            Column {
                // A text with the author name in bold and 16sp font size
                Text(
                    text = "hello",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                // A text with the message body and 8dp of top padding
                Text(
                    text = "Xin chao",
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
@Composable
@Preview(showSystemUi = true, showBackground = true)
fun PreviewDefault()
{
    Box(modifier = Modifier
        .fillMaxSize()
        .background(BlackSecondary))
        //.padding(100.dp)
    {
        MessageScreen_2 {

        }
    }
}