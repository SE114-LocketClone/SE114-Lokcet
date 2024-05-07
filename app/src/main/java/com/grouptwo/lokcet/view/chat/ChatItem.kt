package com.grouptwo.lokcet.view.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.Timestamp
import com.grouptwo.lokcet.R
import com.grouptwo.lokcet.data.model.LatestMessage
import com.grouptwo.lokcet.data.model.User
import com.grouptwo.lokcet.ui.theme.fontFamily
import com.grouptwo.lokcet.utils.toDayMonth
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun ChatItem(
    friend: User,
    chatRoomId: String,
    onSelectChat: (String) -> Unit,
    latestMessage: LatestMessage? = null,
    currentServerTime: Timestamp? = null
) {
    // Chat item
    Row(
        Modifier
            .clickable { onSelectChat(chatRoomId) }
            .padding(16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {
        // Avatar and name, last message and time
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Avatar and name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                // Avatar
                GlideImage(imageModel = { friend.profilePicture },
                    imageOptions = ImageOptions(
                        contentScale = ContentScale.Crop, alignment = Alignment.Center
                    ),
                    requestOptions = {
                        RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop()
                    },
                    loading = {
                        // Show a circular progress indicator when loading.
                        CircularProgressIndicator(
                            modifier = Modifier.size(36.dp), color = Color(0xFFE5A500)
                        )
                    },
                    failure = {
                        // Show a circular progress indicator when loading.
                        Image(
                            painter = painterResource(id = R.drawable.icon_friend),
                            contentDescription = "Friend Icon",
                            modifier = Modifier.size(38.dp)
                        )
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .padding(4.dp)
                        .clip(shape = CircleShape)
                        .border(
                            width = 1.dp, color = Color(0xFFE5A500), shape = CircleShape
                        )
                )
                // Name latest message and time
                Spacer(modifier = Modifier.width(16.dp))
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.Start
                ) {
                    // Name
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = "${friend.firstName} ${friend.lastName}", style = TextStyle(
                                color = Color.White,
                                fontFamily = fontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Start
                            )
                        )
                        // Last message and time
                        Spacer(modifier = Modifier.width(8.dp))
                        if (latestMessage != null && currentServerTime != null) {
                            Text(
                                text = latestMessage.message.createdAt.toDayMonth(
                                    currentServerTime
                                ), style = TextStyle(
                                    color = Color(0xFF948F8F),
                                    fontFamily = fontFamily,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Start
                                )
                            )
                        }
                    }
                    // Last message
                    val messageContent = // Has latest message
                        latestMessage?.message?.messageContent ?: // No latest message
                        "Chưa có tin nhắn"
                    // Color if seen or not
                    val colorMessage = if (latestMessage?.message?.seenAt == null) {
                        // Not seen
                        Color.White
                    } else {
                        // Seen
                        Color(0xFF948F8F)
                    }
                    Text(
                        text = messageContent, style = TextStyle(
                            color = colorMessage,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Start
                        )
                    )
                }
                // Arrow icon right
                Spacer(modifier = Modifier.weight(1f))
                Image(
                    painter = painterResource(id = R.drawable.right_direction),
                    contentDescription = "Arrow Icon",
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }
        }
    }
}