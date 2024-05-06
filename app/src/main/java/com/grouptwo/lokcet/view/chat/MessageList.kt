package com.grouptwo.lokcet.view.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.grouptwo.lokcet.data.model.Message
import com.grouptwo.lokcet.data.model.User
import com.grouptwo.lokcet.ui.theme.fontFamily

@Composable
fun MessageList(
    messageList: List<Message>,
    currentUser: User,
    friendMap: Map<String, User>,
) {

    // Check if message is empty
    if (messageList.isEmpty()) {
        // Show empty message
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Chưa có tin nhắn nào\nHãy bắt đầu cuộc trò chuyện nào!",
                style = TextStyle(
                    color = Color.Gray,
                    fontSize = 16.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
            )
        }
    } else {
        val lazyMessageList = rememberLazyListState()
        //Always scroll to the bottom of the list when new message is added
        LaunchedEffect(key1 = messageList.size) {
            lazyMessageList.scrollToItem(messageList.size)
        }
        LazyColumn(
            state = lazyMessageList,
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(messageList) { index, message ->
                // Check the next message is from the same friend or not
                // If not then not show friend avatar in the message item
                // only show on the last message of continuous messages from the same friend
//                val isShowFriendAvatar = if (message.senderId != currentUser.id) {
//                    if (index == messageList.size - 1 && message.senderId != currentUser.id) {
//                        true
//                    } else if (index < messageList.size - 1 && message.senderId != currentUser.id && messageList[index + 1].senderId != message.senderId) {
//                        true
//                    } else if (index < messageList.size - 1 && message.senderId != currentUser.id && messageList[index + 1].senderId == message.senderId) {
//                        false
//                    } else {
//                        false
//                    }
//                } else {
//                    false
//                }
                val isShowFriendAvatar = message.senderId != currentUser.id &&
                        (index == messageList.size - 1 || message.senderId != messageList[index + 1].senderId)
                // Display time stamp for the first message of the day
                // if the message is the first message of the list then show the time stamp
                // if the message is not the first message of the list then compare the time with the previous message if the difference is more than 24 hours then show the time stamp
                val isShowTimeStamp = if (index == 0) {
                    true
                } else {
                    val previousMessage = messageList[index - 1]
                    val previousMessageTime = previousMessage.createdAt
                    val currentMessageTime = message.createdAt
                    val diff = currentMessageTime.time - previousMessageTime.time
                    when {
                        diff > 24 * 60 * 60 * 1000 -> true // If the difference is more than 24 hours then show the time stamp
                        else -> false
                    }
                }
                MessageItem(
                    message = message,
                    shouldShowFriendAvatar = isShowFriendAvatar,
                    currentUser = currentUser,
                    isShowTimeStamp = isShowTimeStamp,
                    friendMap = friendMap
                )
            }
        }
    }
}