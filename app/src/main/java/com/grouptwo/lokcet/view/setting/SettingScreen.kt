package com.grouptwo.lokcet.view.setting

import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.grouptwo.lokcet.R
import com.grouptwo.lokcet.ui.component.global.composable.BasicIconButton
import com.grouptwo.lokcet.ui.component.global.ime.rememberImeState
import com.grouptwo.lokcet.ui.theme.fontFamily
import com.grouptwo.lokcet.utils.noRippleClickable
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun SettingScreen(
    viewModel: SettingViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val imeState = rememberImeState()
    val uiState = viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = imeState.value) {
        if (imeState.value) {
            scrollState.animateScrollTo(scrollState.maxValue, tween(300))
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
        ) {
            if (uiState.value.isLoadingUserData) {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(40.dp), color = Color(0xFFE5A500)
                    )
                }
            } else {
                BasicIconButton(
                    drawableResource = R.drawable.arrow_left,
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.Start),
                    action = { },
                    description = "Back icon",
                    colors = Color(0xFF948F8F),
                    tint = Color.White
                )

                // Avatar
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .clip(shape = CircleShape)
                            .border(
                                width = 1.dp, color = Color(0xFFE5A500), shape = CircleShape
                            )
                            .clickable {
                                // Open bottom sheet to change avatar
                            }, contentAlignment = Alignment.Center
                    ) {
                        GlideImage(
                            imageModel = {
                                uiState.value.currentUser?.profilePicture ?: ""
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
                                    modifier = Modifier.size(18.dp), color = Color(0xFFE5A500)
                                )
                            },
                            failure = {
                                // Show a circular progress indicator when loading.
                                Image(
                                    painter = painterResource(id = R.drawable.icon_friend),
                                    contentDescription = "Friend Icon",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(1.dp)
                                )
                            },
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(shape = CircleShape)
                                .aspectRatio(1f)
                                .padding(1.dp)

                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    // User name
                    Text(
                        text = "${uiState.value.currentUser?.firstName} ${uiState.value.currentUser?.lastName}",
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = Color.White

                        ),
                        textAlign = TextAlign.Center,
                    )
                }

                // Overall setting title
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_general),
                        contentDescription = "General Icon",
                        modifier = Modifier.size(18.dp),
                        colorFilter = ColorFilter.tint(Color(0xFF959292))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tổng quát",
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF959292)
                        ),
                    )
                }
                // Overall setting
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(20))
                        .background(color = Color(0xFF272626))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Change name
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .noRippleClickable {
                                    // Open new screen to change name
                                }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(25.dp)
                                        .clip(CircleShape)
                                        .background(color = Color(0xFF545252)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.icon_name),
                                        contentDescription = "Name Icon",
                                        modifier = Modifier.size(14.dp),
                                        alignment = Alignment.Center,
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Thay đổi tên",
                                    style = TextStyle(
                                        fontFamily = fontFamily,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 16.sp,
                                        color = Color.White
                                    ),
                                )
                            }
                            Image(
                                painter = painterResource(id = R.drawable.right_direction),
                                contentDescription = "Arrow Right Icon",
                                modifier = Modifier.size(18.dp),
                                colorFilter = ColorFilter.tint(Color(0xFF959292))
                            )
                        }
                        // Change phone number
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .noRippleClickable {
                                    // Open new screen to change name
                                }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(25.dp)
                                        .clip(CircleShape)
                                        .background(color = Color(0xFF545252)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.icon_phone),
                                        contentDescription = "Phone Icon",
                                        modifier = Modifier.size(14.dp),
                                        alignment = Alignment.Center,
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Thay đổi số điện thoại",
                                    style = TextStyle(
                                        fontFamily = fontFamily,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 16.sp,
                                        color = Color.White
                                    ),
                                )
                            }
                            Image(
                                painter = painterResource(id = R.drawable.right_direction),
                                contentDescription = "Arrow Right Icon",
                                modifier = Modifier.size(18.dp),
                                colorFilter = ColorFilter.tint(Color(0xFF959292))
                            )
                        }
                        // Report a problem
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .noRippleClickable {
                                    // Open new screen to change name
                                }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(25.dp)
                                        .clip(CircleShape)
                                        .background(color = Color(0xFF545252)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.icon_report),
                                        contentDescription = "Report Icon",
                                        modifier = Modifier.size(14.dp),
                                        alignment = Alignment.Center,
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Report a problem",
                                    style = TextStyle(
                                        fontFamily = fontFamily,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 16.sp,
                                        color = Color.White
                                    ),
                                )
                            }
                            Image(
                                painter = painterResource(id = R.drawable.right_direction),
                                contentDescription = "Arrow Right Icon",
                                modifier = Modifier.size(18.dp),
                                colorFilter = ColorFilter.tint(Color(0xFF959292))
                            )
                        }
                        // Make a suggestion
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .noRippleClickable {
                                    // Open new screen to change name
                                }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(25.dp)
                                        .clip(CircleShape)
                                        .background(color = Color(0xFF545252)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.icon_suggestion),
                                        contentDescription = "Suggestion Icon",
                                        modifier = Modifier.size(14.dp),
                                        alignment = Alignment.Center,
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Make a suggestion",
                                    style = TextStyle(
                                        fontFamily = fontFamily,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 16.sp,
                                        color = Color.White
                                    ),
                                )
                            }
                            Image(
                                painter = painterResource(id = R.drawable.right_direction),
                                contentDescription = "Arrow Right Icon",
                                modifier = Modifier.size(18.dp),
                                colorFilter = ColorFilter.tint(Color(0xFF959292))
                            )
                        }
                    }
                }
                // Dangerous setting title
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_dangerous),
                        contentDescription = "Dangerous Icon",
                        modifier = Modifier.size(18.dp),
                        colorFilter = ColorFilter.tint(Color(0xFF959292))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Vùng nguy hiểm",
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF959292)
                        ),
                    )
                }
                // Dangerous setting
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(20))
                        .background(color = Color(0xFF272626))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Delete account
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .noRippleClickable {
                                    // Open new screen to change name
                                }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(25.dp)
                                        .clip(CircleShape)
                                        .background(color = Color(0xFF545252)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.icon_delete),
                                        contentDescription = "Delete Icon",
                                        modifier = Modifier.size(14.dp),
                                        alignment = Alignment.Center,
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Xóa tài khoản",
                                    style = TextStyle(
                                        fontFamily = fontFamily,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 16.sp,
                                        color = Color.White
                                    ),
                                )
                            }
                            Image(
                                painter = painterResource(id = R.drawable.right_direction),
                                contentDescription = "Arrow Right Icon",
                                modifier = Modifier.size(18.dp),
                                colorFilter = ColorFilter.tint(Color(0xFF959292))
                            )
                        }
                        // Log out
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .noRippleClickable {
                                    // Open new screen to change name
                                }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(25.dp)
                                        .clip(CircleShape)
                                        .background(color = Color(0xFF545252)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.icon_logout),
                                        contentDescription = "Logout Icon",
                                        modifier = Modifier.size(14.dp),
                                        alignment = Alignment.Center,
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Đăng xuất",
                                    style = TextStyle(
                                        fontFamily = fontFamily,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 16.sp,
                                        color = Color.White
                                    ),
                                )
                            }
                            Image(
                                painter = painterResource(id = R.drawable.right_direction),
                                contentDescription = "Arrow Right Icon",
                                modifier = Modifier.size(18.dp),
                                colorFilter = ColorFilter.tint(Color(0xFF959292))
                            )
                        }
                    }
                }
            }
        }
    }
}
