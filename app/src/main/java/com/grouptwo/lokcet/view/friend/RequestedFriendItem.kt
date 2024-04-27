package com.grouptwo.lokcet.view.friend

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.grouptwo.lokcet.R
import com.grouptwo.lokcet.data.model.User
import com.grouptwo.lokcet.ui.theme.BlackSecondary
import com.grouptwo.lokcet.ui.theme.fontFamily
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun RequestedFriendItem(
    user: User,
    onAcceptFriend: (User) -> Unit,
    onRemoveFriend: (User) -> Unit,
    isAcceptingRequestFriend: Boolean,
    hasAcceptRequestFriendSuccess: Boolean,
    isRemovingRequestedFriend: Boolean,
    hasRemoveRequestedFriendSuccess: Boolean
) {
    // Only render this item when hasAcceptRequestFriendSuccess and hasRemoveRequestedFriendSuccess are false
    if (!hasAcceptRequestFriendSuccess && !hasRemoveRequestedFriendSuccess) {
        // Render the requested friend item
        Row(
            modifier = Modifier.fillMaxWidth() .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                GlideImage(
                    imageModel = { user.profilePicture },
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
                            modifier = Modifier
                                .size(36.dp)
                                .padding(2.dp)
                        )
                    },
                    modifier = Modifier
                        .size(38.dp)
                        .padding(4.dp)
                        .clip(shape = CircleShape)
                        .border(
                            width = 1.dp, color = Color(0xFFE5A500), shape = CircleShape
                        )
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    modifier = Modifier.padding(start = 20.dp),
                    text = "${user.firstName} ${user.lastName}",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFEDEDED)
                    )
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                // Accept friend button
                Button(
                    modifier = Modifier.padding(8.dp),
                    onClick = {
                        onAcceptFriend(user)
                    },
                    colors = ButtonDefaults.buttonColors(Color(color = 0xFFE5A500)),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (isAcceptingRequestFriend) {
                            CircularProgressIndicator(
                                color = BlackSecondary, modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.icon_check),
                                contentDescription = "Checked Icon",
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Đồng ý", style = TextStyle(
                                    fontSize = 16.sp,
                                    fontFamily = fontFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF000000)
                                )
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                // Remove friend request button

                FloatingActionButton(
                    onClick = {
                        onRemoveFriend(user)
                    },
                    shape = CircleShape,
                    containerColor = Color(0xFF272626), contentColor = Color.White,
                ) {
                    if (isRemovingRequestedFriend) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(38.dp), color = Color.White
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.icon_close),
                            contentDescription = "Remove",
                            modifier = Modifier.size(17.dp),
                            alignment = Alignment.Center,
                            colorFilter = ColorFilter.tint(Color.White)
                        )
                    }
                }
            }
        }
    }
}