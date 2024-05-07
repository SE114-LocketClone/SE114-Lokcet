package com.grouptwo.lokcet.view.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.grouptwo.lokcet.R
import com.grouptwo.lokcet.ui.component.global.composable.BasicIconButton
import com.grouptwo.lokcet.ui.theme.YellowPrimary
import com.grouptwo.lokcet.ui.theme.fontFamily
import com.grouptwo.lokcet.utils.DataState
import com.grouptwo.lokcet.utils.getFriendId

@Composable
fun ChatScreen1(
    viewModel: ChatViewModel = hiltViewModel(),
    popUp: () -> Unit,
    navigate: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

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
                        .size(40.dp)
                        .align(Alignment.Start),
                    action = { viewModel.onBackClick(popUp) },
                    description = "Back icon",
                    colors = Color(0xFF948F8F),
                    tint = Color.White
                )
                // Chat screen content
                // Tittle
                Text(
                    text = "Tin nhắn",
                    style = TextStyle(
                        color = Color.White,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp,
                        textAlign = TextAlign.Center
                    )
                )
            }
            // Check the chat room is empty or not
            when (uiState.value.friendList) {
                is DataState.Loading -> {
                    // Loading state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
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

                is DataState.Success -> { // Get all data successfully (friend list, chat room list, latest message map)
                    // Success state
                    Column(
                        modifier = Modifier
                            .verticalScroll(scrollState)
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        if (uiState.value.chatRoomList.isEmpty()) {
                            // No chat room
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Không có tin nhắn",
                                    style = TextStyle(
                                        color = Color.White,
                                        fontFamily = fontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp,
                                        textAlign = TextAlign.Center
                                    )
                                )
                            }
                        } else {
                            // Have chat room then display the chat room list
                            uiState.value.chatRoomList.forEach { chatRoom ->
                                ChatItem(
                                    chatRoomId = chatRoom.chatRoomId,
                                    friend = uiState.value.friendMap[uiState.value.currentUser?.id?.let {
                                        chatRoom.chatRoomId.getFriendId(
                                            it
                                        )
                                    }]!!,
                                    latestMessage = uiState.value.latestMessageMap[chatRoom.chatRoomId]?.latestMessage,
                                    currentServerTime = uiState.value.currentServerTime,
                                    onSelectChat = { chatRoomId ->
                                        viewModel.onChatItemClick(chatRoomId, navigate)
                                    }
                                )
                            }
                        }
                    }
                }

                is DataState.Error -> {
                    // Error state
                    Text(
                        text = "Đã xảy ra lỗi khi tải dữ liệu",
                        style = TextStyle(
                            color = Color.White,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }
        }
    }
}
