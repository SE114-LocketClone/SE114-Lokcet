package com.grouptwo.lokcet.view.feed

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.grouptwo.lokcet.R
import com.grouptwo.lokcet.data.model.User
import com.grouptwo.lokcet.ui.theme.YellowPrimary
import com.grouptwo.lokcet.ui.theme.fontFamily
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendDropdown(
    selectedFriend: User?,
    friendList: List<User>,
    onFriendSelected: (User?) -> Unit,
) {
    var showDropdown by rememberSaveable { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    // Button to display the selected friend and show the dropdown menu
    val selectedFriendName =
        if (selectedFriend == null) "Mọi người" else "${selectedFriend.firstName} ${selectedFriend.lastName}"
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                showDropdown = !showDropdown
            },
            modifier = Modifier.wrapContentSize(),
            colors = ButtonDefaults.buttonColors(Color(0xFF272626)),
            shape = RoundedCornerShape(50.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = selectedFriendName, style = TextStyle(
                        color = Color.White,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )
                Spacer(modifier = Modifier.size(4.dp))
                Image(
                    painter = painterResource(id = R.drawable.arrow_down),
                    contentDescription = "Friend Logo",
                    modifier = Modifier.size(20.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }
        }
        Spacer(modifier = Modifier.size(16.dp))
        // Dropdown menu to select a friend
        Box(
        ) {
            if (showDropdown) {
                Popup(
                    alignment = Alignment.TopCenter,
                    properties = PopupProperties(
                        excludeFromSystemGesture = true,
                    ),
                    onDismissRequest = {
                        showDropdown = false
                    },
                ) {
                    Column(
                        modifier = Modifier
                            .heightIn(max = 200.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 64.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF272626))
                            .verticalScroll(scrollState),
                        horizontalAlignment = Alignment.Start,
                    ) {
                        // All friends option
                        Box(modifier = Modifier
                            .clickable {
                                onFriendSelected(null)
                                showDropdown = false
                            }
                            .padding(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(), // This line is added
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Start,
                                ) {
                                    Box {
                                        Image(
                                            painter = painterResource(id = R.drawable.icon_people),
                                            contentDescription = "Friend Logo",
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(shape = CircleShape)
                                                .border(
                                                    width = 1.dp,
                                                    color = YellowPrimary,
                                                    shape = CircleShape
                                                )
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        text = "Mọi người", style = TextStyle(
                                            color = Color.White,
                                            fontFamily = fontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                    )
                                }
                                Image(
                                    painter = painterResource(id = R.drawable.arrow_right),
                                    contentDescription = "Next Icon",
                                    modifier = Modifier.size(20.dp),
                                    colorFilter = ColorFilter.tint(Color.White)
                                )
                            }
                        }
                        Divider(
                            color = Color(0xFF948F8F),
                            thickness = 1.dp,
                            modifier = Modifier.fillMaxWidth()
                        )
                        // Friends list
                        friendList.onEachIndexed { index, item ->
                            if (index != 0) {
                                Divider(
                                    color = Color(0xFF948F8F),
                                    thickness = 1.dp,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clickable {
                                        onFriendSelected(item)
                                        showDropdown = false
                                    }
                                    .padding(8.dp),
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Start,
                                    ) {
                                        Box {
                                            GlideImage(
                                                imageModel = {
                                                    item.profilePicture
                                                },
                                                imageOptions = ImageOptions(
                                                    contentScale = ContentScale.Crop,
                                                    alignment = Alignment.Center
                                                ),
                                                requestOptions = {
                                                    RequestOptions().diskCacheStrategy(
                                                        DiskCacheStrategy.ALL
                                                    ).centerCrop()
                                                },
                                                loading = {
                                                    // Show a circular progress indicator when loading.
                                                    CircularProgressIndicator(
                                                        modifier = Modifier.size(36.dp),
                                                        color = Color(0xFFE5A500)
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
                                                    .size(38.dp)
                                                    .clip(shape = CircleShape)
                                                    .border(
                                                        width = 2.dp,
                                                        color = YellowPrimary,
                                                        shape = CircleShape
                                                    )
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Text(
                                            text = "${item.firstName} ${item.lastName}",
                                            style = TextStyle(
                                                color = Color.White,
                                                fontFamily = fontFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp
                                            )
                                        )
                                    }
                                    Image(
                                        painter = painterResource(id = R.drawable.arrow_right),
                                        contentDescription = "Next Icon",
                                        modifier = Modifier.size(20.dp),
                                        colorFilter = ColorFilter.tint(Color.White)
                                    )
                                }
                            }
                        }
                    }
                }

            }

        }
    }
}
