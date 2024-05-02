package com.grouptwo.lokcet.view.feed

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grouptwo.lokcet.R

@Composable
fun ReactionBar(
    showRelyFeedTextField: MutableState<Boolean>
) {
    if (!showRelyFeedTextField.value) {
        Box(
            modifier = Modifier
                .width(200.dp)
                .clip(shape = RoundedCornerShape(50.dp))
                .background(color = Color(0xFF272626))

        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.icon_chat),
                    contentDescription = "Reply Feed",
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            // Show text field to reply feed
                            showRelyFeedTextField.value = true
                        },
                    colorFilter = ColorFilter.tint(Color(0xFFACA4A4)),
                )
                // Emoji 💛
                Text(text = "💛", fontSize = 20.sp, modifier = Modifier.clickable {
                    // Show emoji picker
                })
                // Emoji 🔥
                Text(text = "🔥", fontSize = 20.sp, modifier = Modifier.clickable {
                    // Show icon picker
                })
                // Emoji 😂
                Text(text = "😂", fontSize = 20.sp, modifier = Modifier.clickable {
                    // Show icon picker
                })
                // Emoji picker
                Image(
                    painter = painterResource(id = R.drawable.add_emoji),
                    contentDescription = "Emoji Picker",
                    modifier = Modifier
                        .clickable {
                            // Show emoji picker
                        }
                        .size(40.dp),
                )
            }
        }
    }
}