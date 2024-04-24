package com.grouptwo.lokcet.view.home


import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.grouptwo.lokcet.R
import com.grouptwo.lokcet.ui.theme.YellowPrimary
import com.grouptwo.lokcet.ui.theme.fontFamily
import com.grouptwo.lokcet.utils.DataState
import com.grouptwo.lokcet.utils.noRippleClickable

@Composable
fun HomeScreen2(
    viewModel: HomeViewModel = hiltViewModel(), clearAndNavigate: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val image = uiState.capturedImage?.asImageBitmap()
    // Display the home screen
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Gửi đến...",
                style = TextStyle(
                    fontSize = 22.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB3B3B3),
                    textAlign = TextAlign.Center,
                )
            )
            Spacer(modifier = Modifier.weight(0.1f))
            Box(
                modifier = Modifier
                    .weight(0.65f)
                    .fillMaxWidth()
                    .requiredHeight(385.dp)
            ) {
                image?.let { BitmapPainter(it) }?.let {
                    Image(
                        painter = it,
                        contentDescription = "User Image",
                        modifier = Modifier
                            .fillMaxSize()
                            .heightIn(max = 385.dp)
                            .clip(
                                RoundedCornerShape(20)
                            ),
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center
                    )
                }
                TextField(
                    singleLine = true,
                    shape = RoundedCornerShape(50.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color(0xB2D9D9D9),
                        disabledTextColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    value = uiState.imageCaption,
                    onValueChange = {
                        // Limit the caption to 512 characters
                        if (it.length <= 512) {
                            viewModel.onInputCaption(it)
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(8.dp)
                        .widthIn(min = 86.dp),
                    placeholder = {
                        Text(
                            text = "Thêm một tin nhắn",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontFamily = fontFamily,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF272626),
                                textAlign = TextAlign.Center,
                            )
                        )
                    },
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                )
            }
            Spacer(modifier = Modifier.weight(0.1f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.icon_close),
                    contentDescription = "Close Icon",
                    modifier = Modifier
                        .size(38.dp)
                        .noRippleClickable {
                            viewModel.onClearImage(
                                clearAndNavigate
                            )
                        }
                )
                when (uiState.isImageUpload) {
                    is DataState.Loading -> {
                        Box(
                            modifier = Modifier
                                .size(75.dp)
                                .border(
                                    width = 5.dp,
                                    color = Color.White,
                                    shape = CircleShape
                                )
                        ) {
                            CircularProgressIndicator(
                                color = YellowPrimary,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(5.dp)
                            )
                        }
                    }

                    is DataState.Error -> TODO()
                    is DataState.Success -> {
                        Image(
                            painter = painterResource(id = R.drawable.sent_successful),
                            contentDescription = "Upload Icon",
                            modifier = Modifier
                                .size(75.dp)
                        )
                    }

                    null -> {
                        Image(
                            painter = painterResource(id = R.drawable.icon_upload),
                            contentDescription = "Send Icon",
                            modifier = Modifier
                                .size(75.dp)
                                .noRippleClickable {
                                    viewModel.onClickToUploadImage(clearAndNavigate)
                                }
                        )
                    }
                }
                Image(
                    painter = painterResource(id = R.drawable.save_image),
                    contentDescription = "Send Icon",
                    modifier = Modifier
                        .size(40.dp)
                        .noRippleClickable {
                        }
                )
            }
            Spacer(modifier = Modifier.weight(0.1f))
        }
    }
}