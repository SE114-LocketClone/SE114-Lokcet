package com.grouptwo.lokcet.view.feed

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.grouptwo.lokcet.R
import com.grouptwo.lokcet.data.model.Feed
import com.grouptwo.lokcet.data.model.User
import com.grouptwo.lokcet.ui.component.global.composable.BasicIconButton
import com.grouptwo.lokcet.ui.component.global.composable.Particles
import com.grouptwo.lokcet.ui.theme.BlackSecondary
import com.grouptwo.lokcet.ui.theme.YellowPrimary
import com.grouptwo.lokcet.ui.theme.fontFamily
import com.grouptwo.lokcet.utils.DataState
import com.grouptwo.lokcet.utils.calculateTimePassed
import com.grouptwo.lokcet.utils.returnTimeMinutes
import com.grouptwo.lokcet.view.error.ErrorScreen
import com.makeappssimple.abhimanyu.composeemojipicker.ComposeEmojiPickerBottomSheetUI
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class
)
@Composable
fun FeedScreen(
    viewModel: FeedViewModel = hiltViewModel(), clearAndNavigate: (String) -> Unit
) {
    val uiState = viewModel.uiState.collectAsState()
    // Observe the feed state from the view model
    val feedState: LazyPagingItems<Feed> = viewModel.feedState.collectAsLazyPagingItems()
    val pagerState = rememberPagerState(pageCount = { feedState.itemCount })
    // Get the focus manager from the local focus manager
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    // Display the feed screen
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )
    val reactState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )
    val scrollState = rememberScrollState()
val context = LocalContext.current
    // Launch the effect to get current user feed

    when (uiState.value.friendList) {
        is DataState.Success -> {
            // Display the feed screen
            Box(modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {// If the user taps outside the text field, clear the focus
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                        viewModel.onShowRelyFeedTextField(false)
                    })
                    detectDragGestures { change, dragAmount ->
                        if (change.position.y > 0 && pagerState.currentPage == 0) {
                            // Perform navigation back to HomeScreen
                            viewModel.onSwipeBack(clearAndNavigate)
                        }
                    }
                }) {
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
                        // Back to Home Screen
                        BasicIconButton(
                            drawableResource = R.drawable.arrow_left,
                            modifier = Modifier.size(40.dp),
                            action = { viewModel.onSwipeBack(clearAndNavigate) },
                            description = "Back icon",
                            colors = Color(0xFF272626),
                            tint = Color.White
                        )
                        // View from Button (all people or specific people)
                        FriendDropdown(selectedFriend = uiState.value.selectedFriend,
                            friendList = (uiState.value.friendList as DataState.Success<List<User>>).data,
                            onFriendSelected = {
                                // Update the selected friend
                                viewModel.onSelectedFriendChanged(it)
                            })
                        // Chat Button
//                        IconButton(
//                            onClick = { /*TODO*/ }, colors = IconButtonDefaults.iconButtonColors(
//                                Color(0xFF272626)
//                            ), modifier = Modifier
//                                .size(40.dp)
//                                .clip(CircleShape)
//                        ) {
//                            Image(
//                                painter = painterResource(id = R.drawable.icon_chat),
//                                contentDescription = "Chat Logo",
//                                modifier = Modifier.size(30.dp)
//                            )
//                        }
                        BasicIconButton(
                            drawableResource = R.drawable.icon_menu,
                            modifier = Modifier.size(40.dp),
                            action = { viewModel.onShowOptionMenu(true) },
                            description = "Menu icon",
                            colors = Color(0xFF272626),
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(64.dp))
                    // Show the feed
                    // Get the feed state and apply the state to the feed list
                    Box(
                        modifier = Modifier.wrapContentSize(),
                    ) {
                        feedState.loadState.apply {
                            when {
                                refresh is LoadState.Loading -> {
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

                                append is LoadState.Loading -> {
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
                                                text = "Không có bài viết nào", style = TextStyle(
                                                    fontSize = 20.sp,
                                                    fontFamily = fontFamily,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White,
                                                    textAlign = TextAlign.Center
                                                )
                                            )
                                        }
                                    } else {
                                        if (uiState.value.isShowGridView) {
                                            LazyVerticalGrid(
                                                columns = GridCells.Fixed(3),
                                                contentPadding = PaddingValues(16.dp)
                                            ) {
                                                items(feedState.itemCount) { index -> // Show the feed list
                                                    feedState[index]?.let {
                                                        Box(modifier = Modifier
                                                            .aspectRatio(1f)
                                                            .clickable {
                                                                CoroutineScope(Dispatchers.IO).launch {
                                                                    pagerState.scrollToPage(
                                                                        index
                                                                    )
                                                                    viewModel.onShowGridView(
                                                                        false
                                                                    )
                                                                }
                                                            }) {
                                                            GlideImage(
                                                                imageModel = { it.uploadImage.imageUrl },
                                                                modifier = Modifier
                                                                    .heightIn(max = 120.dp)
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
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            // Return VerticalPager with the feed list and the feed state
                                            VerticalPager(
                                                state = pagerState,
                                                pageSpacing = 200.dp,
                                            ) { page ->
                                                // Set current user
                                                val feed = feedState[page]
                                                if (feed != null) {
                                                    Box( // Display the feed image ensuring it is square
                                                        modifier = Modifier
                                                            .aspectRatio(1f)
                                                            .fillMaxWidth()
                                                    ) {
                                                        GlideImage(
                                                            imageModel = { feed.uploadImage.imageUrl },
                                                            modifier = Modifier
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
                                                            Box(
                                                                modifier = Modifier
                                                                    .align(Alignment.BottomCenter)
                                                                    .padding(16.dp)
                                                            ) {
                                                                TextField(
                                                                    singleLine = true,
                                                                    readOnly = true,
                                                                    shape = RoundedCornerShape(50.dp),
                                                                    colors = TextFieldDefaults.textFieldColors(
                                                                        backgroundColor = Color(
                                                                            0xFF272626
                                                                        ).copy(
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
                                                                        .padding(4.dp)
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
                                                    }
                                                    // Show info (who posted the feed and when) this can be swipe up with feed
                                                    Spacer(modifier = Modifier.height(16.dp))
                                                    Box(
                                                        modifier = Modifier.wrapContentSize()
                                                    ) {
                                                        Row(
                                                            modifier = Modifier.padding(16.dp),
                                                            horizontalArrangement = Arrangement.Center,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            if (feed.uploadImage.userName.isNotEmpty()) {
                                                                Text(
                                                                    text = feed.uploadImage.userName,
                                                                    style = TextStyle(
                                                                        fontSize = 16.sp,
                                                                        fontFamily = fontFamily,
                                                                        fontWeight = FontWeight.Bold,
                                                                        color = Color.White,
                                                                    )
                                                                )
                                                                Spacer(modifier = Modifier.width(16.dp))
                                                            }
                                                            Text(
                                                                text = feed.uploadImage.createdAt.calculateTimePassed(
                                                                    uiState.value.currentServerTime!!
                                                                ), style = TextStyle(
                                                                    fontSize = 16.sp,
                                                                    fontFamily = fontFamily,
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = Color(0xFF737070),
                                                                )
                                                            )
                                                        }
                                                    }
                                                    // Show the reaction bar (aka message bar)
                                                    Spacer(modifier = Modifier.height(16.dp))
                                                    // Owner of feed cannot react to their own feed
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        // Show grid view button
                                                        Image(
                                                            painter = painterResource(id = R.drawable.gallery),
                                                            contentDescription = "Grid icon",
                                                            modifier = Modifier
                                                                .size(45.dp)
                                                                .clickable {
                                                                    viewModel.onShowGridView(true)
                                                                },
                                                            colorFilter = ColorFilter.tint(Color.White)
                                                        )
                                                        if (feed.uploadImage.userId != uiState.value.ownerUser?.id) {
                                                            ReactionBar(showRelyFeedTextField = uiState.value.isShowReplyTextField,
                                                                onSelectedEmoji = {
                                                                    // Update the selected emoji
                                                                    viewModel.onEmojiSelected(
                                                                        it, feed
                                                                    )
                                                                },
                                                                showEmojiPicker = uiState.value.isEmojiPickerVisible,
                                                                onShowEmojiPicker = {
                                                                    // Update the emoji picker visibility
                                                                    viewModel.onShowEmojiPicker(it)
                                                                },
                                                                onShowRelyFeedTextField = {
                                                                    // Update the reply feed text field visibility
                                                                    viewModel.onShowRelyFeedTextField(
                                                                        it
                                                                    )
                                                                })
                                                        } else {
                                                            // Display a button to show reaction feed
                                                            Button(
                                                                onClick = {
                                                                    // Show reaction feed
                                                                    viewModel.onShowReactionList(
                                                                        true
                                                                    )
                                                                },
                                                                modifier = Modifier.wrapContentSize(),
                                                                colors = ButtonDefaults.buttonColors(
                                                                    backgroundColor = Color(
                                                                        0xFF272626
                                                                    ), contentColor = Color.White
                                                                ),
                                                                shape = RoundedCornerShape(50.dp)
                                                            ) {
                                                                Row(
                                                                    verticalAlignment = Alignment.CenterVertically,
                                                                    horizontalArrangement = Arrangement.Center,
                                                                    modifier = Modifier.padding(8.dp)
                                                                ) {
                                                                    Text(
                                                                        text = "Activity",
                                                                        style = TextStyle(
                                                                            fontSize = 16.sp,
                                                                            fontFamily = fontFamily,
                                                                            fontWeight = FontWeight.Bold,
                                                                            color = Color.White,
                                                                        )
                                                                    )
                                                                    Spacer(
                                                                        modifier = Modifier.width(
                                                                            8.dp
                                                                        )
                                                                    )
                                                                    Image(
                                                                        painter = painterResource(id = R.drawable.icon_sparkle),
                                                                        contentDescription = "Sparkle Logo",
                                                                        modifier = Modifier.size(20.dp),
                                                                        colorFilter = ColorFilter.tint(
                                                                            Color.White
                                                                        )
                                                                    )
                                                                }
                                                            }
                                                        }
                                                        // Share button
                                                        Image(
                                                            painter = painterResource(id = R.drawable.icon_share),
                                                            contentDescription = "Share icon",
                                                            modifier = Modifier
                                                                .size(45.dp)
                                                                .clickable {
                                                                    feedState[pagerState.currentPage]?.let {
                                                                        viewModel.onShareFeedImage(context,
                                                                            it
                                                                        )
                                                                    }
                                                                },
                                                            colorFilter = ColorFilter.tint(Color.White)
                                                        )
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
                    }
                }

                if (uiState.value.isShowReplyTextField) {
                    // Create a blur around text field
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                color = Color.Black.copy(alpha = 0.7f) // Semi-transparent black background
                            )
                            .padding(16.dp) // Add padding as needed
                    ) {
                        // Force the blur not blur the textfield inside because surface transparent override
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center),
                            color = Color.Transparent, // Transparent background for the Surface
                            tonalElevation = 8.dp // Adjust the elevation value for the desired blur effect
                        ) {
                            // Show text field to reply feed
                            val fabColor =
                                if (uiState.value.isSendButtonEnabled) Color.White else Color(
                                    0xFF272626
                                )
                            TextField(value = uiState.value.reply, onValueChange = {
                                viewModel.onReplyTextChanged(it)
                            }, placeholder = {
                                Text(
                                    text = "Trả lời ${feedState[pagerState.currentPage]?.uploadImage?.userName}...",
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        fontFamily = fontFamily,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFACA4A4),
                                    )
                                )
                            }, singleLine = true, modifier = Modifier
                                .fillMaxWidth()
                                .clip(
                                    RoundedCornerShape(50.dp)
                                )
                                .focusRequester(focusRequester)
                                .align(
                                    Alignment.Center
                                )
                                .zIndex(3f), textStyle = TextStyle(
                                fontSize = 14.sp,
                                fontFamily = fontFamily,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                            ), colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = Color(0xFF272626),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                cursorColor = Color.White,
                                textColor = Color.White
                            ), trailingIcon = {
                                FloatingActionButton(
                                    onClick = {
                                        feedState[pagerState.currentPage]?.let {
                                            viewModel.onSendReply(
                                                feed = it
                                            )
                                        }
                                    },
                                    shape = CircleShape,
                                    containerColor = fabColor,
                                    modifier = Modifier.padding(8.dp)
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.icon_send),
                                        contentDescription = "Send Logo",
                                        modifier = Modifier.size(40.dp),
                                        alignment = Alignment.Center
                                    )
                                }
                            })
                            LaunchedEffect(Unit) {
                                focusRequester.requestFocus()
                            }
                        }
                    }
                }
                // Show the emoji animation
                if (uiState.value.selectedEmoji.isNotEmpty()) {
                    Particles(
                        modifier = Modifier.fillMaxSize(),
                        quantity = 50,
                        emoji = uiState.value.selectedEmoji,
                        visible = true
                    )
                }
                // Show the emoji picker
                if (uiState.value.isEmojiPickerVisible) {
                    ModalBottomSheet(
                        sheetState = sheetState,
                        shape = RoundedCornerShape(20.dp),
                        tonalElevation = 0.dp,
                        onDismissRequest = {
                            viewModel.onShowEmojiPicker(false)
                            viewModel.onSearchEmoji("")
                        },
                        dragHandle = null,
                        windowInsets = WindowInsets(
                            0
                        )
                    ) {
                        Column(
                            modifier = Modifier.wrapContentSize(),
                        ) {
                            ComposeEmojiPickerBottomSheetUI(
                                onEmojiClick = { emoji ->
                                    viewModel.onShowEmojiPicker(false)
                                    feedState[pagerState.currentPage]?.let {
                                        viewModel.onEmojiSelected(
                                            emoji.character, it
                                        )
                                    }
                                },
                                searchText = uiState.value.searchText,
                                updateSearchText = { updatedSearchText ->
                                    viewModel.onSearchEmoji(updatedSearchText)
                                },
                                backgroundColor = BlackSecondary,
                                groupTitleTextColor = Color.White,
                                groupTitleTextStyle = TextStyle(
                                    fontSize = 16.sp,
                                    fontFamily = fontFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                ),
                                searchBarColor = Color.White,
                                modifier = Modifier
                                    .heightIn(max = 400.dp)
                                    .fillMaxWidth()
                            )
                        }
                    }
                }
                // Show emoji reaction
                if (uiState.value.isShowReactionList) {
                    ModalBottomSheet(
                        onDismissRequest = {
                            viewModel.onShowReactionList(false)

                        },
                        sheetState = reactState,
                        shape = RoundedCornerShape(20.dp),
                        tonalElevation = 0.dp,
                        dragHandle = null,
                        windowInsets = WindowInsets(
                            0
                        ),
                        containerColor = BlackSecondary
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 400.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(scrollState),
                                verticalArrangement = Arrangement.Top,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Reaction", style = TextStyle(
                                        fontSize = 20.sp,
                                        fontFamily = fontFamily,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        textAlign = TextAlign.Center
                                    )
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                if (feedState[pagerState.currentPage]?.emojiReactions!!.isEmpty()) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Không có react nào", style = TextStyle(
                                                fontSize = 16.sp,
                                                fontFamily = fontFamily,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                textAlign = TextAlign.Center
                                            )
                                        )
                                    }
                                } else {
                                    // Show the emoji reaction list using the feed state
                                    feedState[pagerState.currentPage]?.emojiReactions?.forEach {
                                        Row(
                                            modifier = Modifier
                                                .padding(16.dp)
                                                .fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                horizontalArrangement = Arrangement.Start,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                GlideImage(
                                                    imageModel = { uiState.value.friendAvatar[it.userId] },
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
                                                            width = 1.dp,
                                                            color = Color(0xFFE5A500),
                                                            shape = CircleShape
                                                        )
                                                )
                                                Text(
                                                    modifier = Modifier.padding(start = 20.dp),
                                                    text = it.userName,
                                                    style = TextStyle(
                                                        fontSize = 20.sp,
                                                        fontFamily = fontFamily,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFFEDEDED)
                                                    )
                                                )
                                            }
                                            Row(
                                                horizontalArrangement = Arrangement.End,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                // Show the time sent
                                                Text(
                                                    text = returnTimeMinutes(it.createdAt),
                                                    style = TextStyle(
                                                        fontSize = 16.sp,
                                                        fontFamily = fontFamily,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF737070),
                                                    )
                                                )
                                                Spacer(modifier = Modifier.width(16.dp))
                                                Text(
                                                    text = it.emojiId,
                                                    fontSize = 30.sp,
                                                    modifier = Modifier.padding(4.dp),
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                // Show option menu
                if (uiState.value.showOptionMenu) {
                    ModalBottomSheet(
                        sheetState = reactState,
                        shape = RoundedCornerShape(20.dp),
                        tonalElevation = 0.dp,
                        onDismissRequest = {
                            viewModel.onShowOptionMenu(false)
                        },
                        dragHandle = null,
                        windowInsets = WindowInsets(
                            0
                        ),
                        containerColor = BlackSecondary
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 100.dp)
                        ) {
                            Column(
                                modifier = Modifier.wrapContentSize()
                            ) {
                                Button(
                                    onClick = {
                                        feedState[pagerState.currentPage]?.let {
                                            viewModel.downloadImage(
                                                it
                                            )
                                        }
                                    }, colors = ButtonDefaults.buttonColors(
                                        backgroundColor = BlackSecondary, contentColor = Color.White
                                    ), modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = "Tải ảnh xuống", style = TextStyle(
                                            fontSize = 16.sp,
                                            fontFamily = fontFamily,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            textAlign = TextAlign.Center
                                        )
                                    )
                                }
                            }
                        }
                    }
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