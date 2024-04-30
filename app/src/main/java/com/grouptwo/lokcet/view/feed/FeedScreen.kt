package com.grouptwo.lokcet.view.feed

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
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
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.grouptwo.lokcet.R
import com.grouptwo.lokcet.data.model.Feed
import com.grouptwo.lokcet.data.model.User
import com.grouptwo.lokcet.ui.theme.YellowPrimary
import com.grouptwo.lokcet.ui.theme.fontFamily
import com.grouptwo.lokcet.utils.DataState
import com.grouptwo.lokcet.utils.noRippleClickable
import com.grouptwo.lokcet.view.error.ErrorScreen
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FeedScreen(
    viewModel: FeedViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()
    // Observe the feed state from the view model
    val feedState: LazyPagingItems<Feed> = viewModel.feedState.collectAsLazyPagingItems()
    val pagerState = rememberPagerState(pageCount = { feedState.itemCount })

    // Display the feed screen

    when (uiState.value.friendList) {
        is DataState.Success -> {
            // Display the feed screen
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .imePadding(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // User Section
                        Image(painter = painterResource(id = R.drawable.icon_friend),
                            contentDescription = "User Logo",
                            colorFilter = ColorFilter.tint(Color(0xFF272626)),
                            modifier = Modifier
                                .size(50.dp)
                                .noRippleClickable {
                                    // Navigate to the user profile screen
                                })
                        // View from Button (all people or specific people)
                        FriendDropdown(selectedFriend = uiState.value.selectedFriend,
                            friendList = (uiState.value.friendList as DataState.Success<List<User>>).data,
                            onFriendSelected = {
                                // Update the selected friend
                                viewModel.onSelectedFriendChanged(it)
                            })
                        // Chat Button
                        IconButton(
                            onClick = { /*TODO*/ }, colors = IconButtonDefaults.iconButtonColors(
                                Color(0xFF272626)
                            ), modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.icon_chat),
                                contentDescription = "Chat Logo",
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.weight(0.1f))
                    // Show the feed
                    // Get the feed state and apply the state to the feed list

                    Box(
                        modifier = Modifier
                            .weight(0.65f)
                            .requiredHeight(385.dp)
                    ) {
                        feedState.loadState.apply {
                            when {
                                refresh is LoadState.Loading -> {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .align(
                                                Alignment.Center
                                            ), color = YellowPrimary
                                    )
                                }

                                append is LoadState.Loading -> {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .align(
                                                Alignment.Center
                                            ), color = YellowPrimary
                                    )
                                }

                                refresh is LoadState.Error -> {
                                    ErrorScreen(errorMessage = "Lỗi xảy ra khi tải dữ liệu feed",
                                        onRetry = {
                                            feedState.refresh()
                                        })
                                }

                                append is LoadState.Error -> {
                                    ErrorScreen(errorMessage = "Lỗi xảy ra khi tải dữ liệu feed",
                                        onRetry = {
                                            feedState.refresh()
                                        })
                                }

                                else -> {
                                    if (feedState.itemCount == 0) {
                                        // Display an empty feed screen
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "Không có bài viết nào",
                                                style = TextStyle(
                                                    fontSize = 20.sp,
                                                    fontFamily = fontFamily,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White,
                                                    textAlign = TextAlign.Center
                                                )
                                            )
                                        }
                                    } else {
                                        // Return VerticalPager with the feed list and the feed state
                                        VerticalPager(
                                            state = pagerState,
                                            modifier = Modifier.fillMaxSize()
                                        ) { page ->
                                            val feed = feedState[page]
                                            if (feed != null) {
                                                Box {
                                                    GlideImage(
                                                        imageModel = { feed.uploadImage.imageUrl },
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .heightIn(max = 385.dp)
                                                            .clip(
                                                                RoundedCornerShape(20)
                                                            ),
                                                        imageOptions = ImageOptions(
                                                            contentScale = ContentScale.Crop,
                                                            alignment = Alignment.Center
                                                        ),
                                                        requestOptions = {
                                                            RequestOptions().diskCacheStrategy(
                                                                DiskCacheStrategy.ALL
                                                            ).centerCrop()
                                                        },
                                                    )
                                                    if (feed.uploadImage.imageCaption.isNotEmpty()) {
                                                        TextField(
                                                            singleLine = true,
                                                            readOnly = true,
                                                            shape = RoundedCornerShape(50.dp),
                                                            colors = TextFieldDefaults.textFieldColors(
                                                                backgroundColor = Color(0xFF272626).copy(
                                                                    alpha = 0.3f
                                                                ),
                                                                disabledTextColor = Color.Transparent,
                                                                focusedIndicatorColor = Color.Transparent,
                                                                unfocusedIndicatorColor = Color.Transparent,
                                                                disabledIndicatorColor = Color.Transparent
                                                            ),
                                                            value = feed.uploadImage.imageCaption,
                                                            onValueChange = {},
                                                            modifier = Modifier
                                                                .align(Alignment.BottomCenter)
                                                                .padding(8.dp)
                                                                .widthIn(min = 86.dp),
                                                            textStyle = TextStyle(
                                                                fontSize = 14.sp,
                                                                fontFamily = fontFamily,
                                                                fontWeight = FontWeight.Bold,
                                                                color = Color.White,
                                                                textAlign = TextAlign.Center
                                                            )
                                                        )
                                                    }
                                                }
                                            } else {
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
                    }
                    Spacer(modifier = Modifier.weight(0.1f))
                }
            }
        }

        is DataState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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

        is DataState.Error -> {
            // Display the error screen
        }
    }

}