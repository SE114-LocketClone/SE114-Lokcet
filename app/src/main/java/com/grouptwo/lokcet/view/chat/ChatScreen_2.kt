package com.grouptwo.lokcet.view.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.grouptwo.lokcet.R
import com.grouptwo.lokcet.ui.component.global.composable.BasicIconButton
import com.grouptwo.lokcet.ui.theme.YellowPrimary
import com.grouptwo.lokcet.ui.theme.fontFamily
import com.grouptwo.lokcet.utils.DataState
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun ChatScreen2(
    viewModel: ChatViewModel = hiltViewModel(), popUp: () -> Unit
) {
    val scrollState = rememberScrollState()
    val uiState = viewModel.uiState.collectAsState()
    Box(modifier = Modifier.fillMaxSize()) {
        // Chat screen
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 16.dp)
        ) {
            // Top bar
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                BasicIconButton(
                    drawableResource = R.drawable.arrow_left,
                    modifier = Modifier
                        .size(30.dp)
                        .align(Alignment.Start),
                    action = { viewModel.onBackClick(popUp) },
                    description = "Back icon",
                    colors = Color(0xFF948F8F),
                    tint = Color.White
                )
                // Chat screen content
                // Tittle
                val userName = "${
                    uiState.value.friendMap[uiState.value.currentUser?.id?.let {
                        uiState.value.selectedChatRoomId.replace(
                            it, ""
                        ).replace("_", "")
                    }]?.firstName
                } ${
                    uiState.value.friendMap[uiState.value.currentUser?.id?.let {
                        uiState.value.selectedChatRoomId.replace(
                            it, ""
                        ).replace("_", "")
                    }]?.lastName
                }"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar
                    GlideImage(
                        imageModel = {
                            uiState.value.friendMap[uiState.value.currentUser?.id?.let {
                                uiState.value.selectedChatRoomId.replace(
                                    it, ""
                                ).replace("_", "")
                            }]?.profilePicture
                        },
                        imageOptions = ImageOptions(
                            contentScale = ContentScale.Crop, alignment = Alignment.Center
                        ),
                        requestOptions = {
                            RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
                                .centerCrop()
                        },
                        loading = {
                            // Show a circular progress indicator when loading.
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp), color = Color(0xFFE5A500)
                            )
                        },
                        failure = {
                            // Show a circular progress indicator when loading.
                            Image(
                                painter = painterResource(id = R.drawable.icon_friend),
                                contentDescription = "Friend Icon",
                                modifier = Modifier.size(36.dp)
                            )
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(shape = CircleShape)
                            .border(
                                width = 1.dp, color = Color(0xFFE5A500), shape = CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // Get the friend name from the selected chat room id and map it to the friend map
                    Text(
                        text = userName, style = TextStyle(
                            color = Color.White,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }
            // Chat content list
            when (val messageList = uiState.value.messageList) {
                is DataState.Success -> {
                    // Chat content list
                    Column(
                        modifier = Modifier
//                            .verticalScroll(scrollState)
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        uiState.value.currentUser?.let {
                            MessageList(
                                messageList = messageList.data,
                                currentUser = it,
                                friendMap = uiState.value.friendMap
                            )
                        }
                    }
                    // Chat input
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        // Send button
                        val fabColor =
                            if (uiState.value.isButtonSendEnable) Color.White else Color(0xFF272626)
                        // TextField
                        TextField(value = uiState.value.messageInput, onValueChange = {
                            viewModel.onMessageChange(it)
                        }, placeholder = {
                            Text(
                                text = "Gửi tin nhắn", style = TextStyle(
                                    fontSize = 16.sp,
                                    fontFamily = fontFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFACA4A4),
                                    textAlign = TextAlign.Justify
                                )
                            )
                        }, maxLines = 5, minLines = 1, modifier = Modifier
                            .clip(
                                RoundedCornerShape(50.dp)
                            )
                            .weight(1f), textStyle = TextStyle(
                            fontSize = 16.sp,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        ), colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color(0xFF272626),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            cursorColor = Color.White,
                            textColor = Color.White
                        ), trailingIcon = {
                            FloatingActionButton(
                                onClick = {
                                    viewModel.onSendClick()
                                },
                                shape = CircleShape,
                                containerColor = fabColor,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(30.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.icon_send),
                                    contentDescription = "Send Logo",
                                    modifier = Modifier.size(24.dp),
                                    alignment = Alignment.Center
                                )
                            }
                        })
                    }
                }

                is DataState.Error -> {
                    Text(
                        text = "Đã xảy ra lỗi khi tải dữ liệu", style = TextStyle(
                            color = Color.White,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center
                        )
                    )
                }

                is DataState.Loading -> {
                    // Loading
                    // Loading state
                    Box(
                        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                    ) {
                        // Display a loading indicator
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(50.dp)
                                .align(
                                    Alignment.Center
                                ), color = YellowPrimary
                        )
                    }
                }
            }
        }
    }
}