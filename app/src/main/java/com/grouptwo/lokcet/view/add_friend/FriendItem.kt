package com.grouptwo.lokcet.view.add_friend

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import com.grouptwo.lokcet.data.model.User
import com.grouptwo.lokcet.ui.theme.BlackSecondary
import com.grouptwo.lokcet.ui.theme.fontFamily
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun FriendItem(
    user: User, action: (User) -> Unit, isAddingFriend: Boolean, hasAddFriendSuccess: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            GlideImage(imageModel = { user.profilePicture }, imageOptions = ImageOptions(
                contentScale = ContentScale.Crop, alignment = Alignment.Center
            ), requestOptions = {
                RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop()
            }, loading = {
                // Show a circular progress indicator when loading.
                CircularProgressIndicator(
                    modifier = Modifier.size(36.dp), color = Color(0xFFE5A500)
                )
            }, failure = {
                // Show a circular progress indicator when loading.
                Image(
                    painter = painterResource(id = R.drawable.icon_friend),
                    contentDescription = "Friend Icon",
                    modifier = Modifier.size(36.dp)
                )
            }, modifier = Modifier
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
        if (hasAddFriendSuccess) {
            // Add a little opacity to the button if the friend has been added
            Button(
                modifier = Modifier
                    .padding(8.dp)
                    .alpha(0.5f),
                onClick = {
                    // Do nothing
                },
                colors = ButtonDefaults.buttonColors(Color(color = 0xFFE5A500)),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_check),
                        contentDescription = "Checked Icon",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Đã thêm", style = TextStyle(
                            fontSize = 16.sp,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF000000)
                        )
                    )
                }
            }
        } else {
            Button(
                modifier = Modifier.padding(8.dp),
                onClick = { action(user) },
                colors = ButtonDefaults.buttonColors(Color(color = 0xFFE5A500))

            ) {
                if (isAddingFriend) {
                    CircularProgressIndicator(
                        color = BlackSecondary, modifier = Modifier.size(20.dp)
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            Icons.Filled.Add,
                            contentDescription = "Add Icon",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Thêm bạn", style = TextStyle(
                                fontSize = 16.sp,
                                fontFamily = fontFamily,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF000000)
                            )
                        )
                    }
                }
            }
        }
    }
}