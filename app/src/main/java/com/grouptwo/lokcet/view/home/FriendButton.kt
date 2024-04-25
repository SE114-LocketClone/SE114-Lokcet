package com.grouptwo.lokcet.view.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.grouptwo.lokcet.R
import com.grouptwo.lokcet.data.model.User
import com.grouptwo.lokcet.ui.theme.YellowPrimary
import com.grouptwo.lokcet.ui.theme.fontFamily
import com.grouptwo.lokcet.utils.noRippleClickable
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun FriendButton(
    user: User, onAddFriend: (String) -> Unit, visibleIds: List<String>? = null
) {
    Column(
        modifier = Modifier
            .noRippleClickable { onAddFriend(user.id) }
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // If the visibleIds is not null and the user id is in the visibleIds list, show yellow border around the profile picture else show gray border.
        val borderColor = if (visibleIds != null && visibleIds.contains(user.id)) {
            YellowPrimary
        } else {
            Color(0xFF948F8F)
        }
        Box {
            GlideImage(imageModel = {
                user.profilePicture
            }, imageOptions = ImageOptions(
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
                    modifier = Modifier
                        .size(36.dp)
                )
            }, modifier = Modifier
                .size(38.dp)
                .clip(shape = CircleShape)
                .border(
                    width = 2.dp, color = borderColor, shape = CircleShape
                )
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "${user.firstName} ${user.lastName}",
            style = TextStyle(
                color = Color.White,
                fontFamily = fontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp, textAlign = TextAlign.Center
            )
        )
    }
}