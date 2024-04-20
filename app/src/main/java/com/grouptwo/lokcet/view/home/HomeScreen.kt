package com.grouptwo.lokcet.view.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.grouptwo.lokcet.R
import com.grouptwo.lokcet.ui.theme.fontFamily
import com.grouptwo.lokcet.utils.noRippleClickable
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage


@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(), navigate: (String) -> Unit
) {
    // Define the state for the swipe gesture
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    // Display the home scree
    Box(modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            // Update the offset value when the user swipes up
            detectDragGestures { change, dragAmount ->
                change.consume()
                val (x, y) = dragAmount
                when {
                    // Only allow the user to swipe up and navigate to the feed screen when the user swipes up
                    // Change in the y direction is negative
                    y < 0 -> {
                        viewModel.onSwipeUp(navigate)
                    }
                }
                offsetX += dragAmount.x
                offsetY += dragAmount.y
            }
        }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(painter = painterResource(id = R.drawable.icon_friend),
                    contentDescription = "User Logo",
                    colorFilter = ColorFilter.tint(Color(0xFF272626)),
                    modifier = Modifier
                        .size(50.dp)
                        .noRippleClickable {
                            // Navigate to the user profile screen
                        })
                Button(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.wrapContentSize(),
                    colors = ButtonDefaults.buttonColors(Color(0xFF272626)),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.icon_people),
                            contentDescription = "Friend Logo",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(
                            text = "Bạn bè", style = TextStyle(
                                color = Color.White,
                                fontFamily = fontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        )
                    }
                }
                IconButton(
                    onClick = { /*TODO*/ }, colors = IconButtonDefaults.iconButtonColors(
                        Color(0xFF272626)
                    ), modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_chat),
                        contentDescription = "Chat Logo",
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.weight(0.1f))
            Box(
                modifier = Modifier
                    .weight(0.65f)
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(20)
                    )
            ) {
                GlideImage(
                    imageModel = { "https://picsum.photos/200/300" },
                    modifier = Modifier.fillMaxSize(),
                    imageOptions = ImageOptions(
                        contentScale = ContentScale.Crop, alignment = Alignment.Center
                    ),
                    requestOptions = {
                        RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop()
                    },
                )
            }
            Spacer(modifier = Modifier.weight(0.05f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(painter = painterResource(id = R.drawable.upload_picture),
                    contentDescription = "Upload Picture",
                    modifier = Modifier
                        .size(50.dp)
                        .noRippleClickable {
                            // Open Image Picker
                        })
                Image(painter = painterResource(id = R.drawable.take_picture),
                    contentDescription = "Take Picture",
                    modifier = Modifier
                        .size(75.dp)
                        .noRippleClickable {
                            // Take a picture of current viewBox
                        })
                Image(painter = painterResource(id = R.drawable.switch_camera),
                    contentDescription = "Switch Camera",
                    modifier = Modifier
                        .size(50.dp)
                        .noRippleClickable {
                            // Switch camera to front or back
                        })
            }
            Spacer(modifier = Modifier.weight(0.1f))
            Column(horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.noRippleClickable {
                    // Navigate to the feed screen
                    viewModel.onSwipeUp(navigate)
                }) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.feed),
                        contentDescription = "Friend Logo",
                        modifier = Modifier.size(25.dp),
                        colorFilter = ColorFilter.tint(Color(0xFF8A8D8E))
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "Feed", style = TextStyle(
                            color = Color.White,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Image(
                    painter = painterResource(id = R.drawable.arrow_down),
                    contentDescription = "Arrow Down",
                    modifier = Modifier.size(32.dp),
                    colorFilter = ColorFilter.tint(Color(0xFF8A8D8E))
                )
            }
        }
    }
}
