package com.grouptwo.lokcet.view.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.grouptwo.lokcet.R
import com.grouptwo.lokcet.ui.theme.fontFamily
import com.grouptwo.lokcet.utils.noRippleClickable
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun HomeScreen() {
    // Display the home screen
    Box(modifier = Modifier.fillMaxSize()) {
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
                    .weight(0.6f)
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
            Spacer(modifier = Modifier.weight(0.1f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.2f)
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
                Image(
                    painter = painterResource(id = R.drawable.take_picture),
                    contentDescription = "Take Picture",
                    modifier = Modifier
                        .size(75.dp)
                        .noRippleClickable {
                            // Take a picture of current viewBox
                        }
                )
                Image(
                    painter = painterResource(id = R.drawable.switch_camera),
                    contentDescription = "Switch Camera",
                    modifier = Modifier
                        .size(50.dp)
                        .noRippleClickable {
                            // Switch camera to front or back
                        }
                )
            }
        }
    }
}