package com.grouptwo.lokcet.view.add_friend


import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.grouptwo.lokcet.R
import com.grouptwo.lokcet.ui.component.global.composable.BasicShareButton
import com.grouptwo.lokcet.ui.component.global.ime.rememberImeState
import com.grouptwo.lokcet.ui.theme.BlackSecondary
import com.grouptwo.lokcet.ui.theme.YellowPrimary
import com.grouptwo.lokcet.ui.theme.fontFamily
import com.grouptwo.lokcet.utils.DataState
import com.grouptwo.lokcet.view.error.ErrorScreen

@Composable
fun AddFriendScreen(
    viewModel: AddFriendViewModel = hiltViewModel(),
    clearAndNavigate: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    val uiState by viewModel.uiState.collectAsState()
    val imeState = rememberImeState()
    val focusManager = LocalFocusManager.current
    var isKeyboardVisible by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = imeState.value) {
        if (imeState.value) {
            scrollState.animateScrollTo(scrollState.maxValue, tween(300))
        }
    }

    when (val state = uiState.filteredSuggestedList) {
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

        is DataState.Success -> {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .imePadding()
                        .padding(start = 16.dp, end = 16.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Column(
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "Kết bạn mới", style = TextStyle(
                                fontSize = 24.sp,
                                fontFamily = fontFamily,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF737070),
                                textAlign = TextAlign.Center,
                            )
                        )
                        Text(
                            text = "Kết bạn với ít nhất một người bạn để tiếp tục",
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontFamily = fontFamily,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        val textFieldColors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF272626),
                            unfocusedContainerColor = Color(0xFF272626),
                            unfocusedIndicatorColor = Color.Black,
                            focusedIndicatorColor = Color.Black,
                        )
                        TextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusChanged { focusState ->
                                    isKeyboardVisible = focusState.isFocused
                                },
                            value = uiState.searchKeyword,
                            onValueChange = {
                                if (it.isEmpty()) {
                                    // Reset the search results
                                    viewModel.performSearch("")
                                }
                                viewModel.onSearchKeywordChanged(it)
                            },
                            placeholder = {
                                Text(
                                    text = "Tìm trong các liên hệ của bạn", style = TextStyle(
                                        fontSize = 20.sp,
                                        fontFamily = fontFamily,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF737070),
                                        textAlign = TextAlign.Center
                                    )
                                )
                            },
                            singleLine = true,
                            leadingIcon = {
                                Image(
                                    painter = painterResource(id = R.drawable.icon_search),
                                    contentDescription = "Search Icon",
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = {
                                focusManager.clearFocus()
                                // Trigger your search operation here when 'Done' is pressed
                                viewModel.performSearch(uiState.searchKeyword)
                            }),
                            trailingIcon = {
                                if (isKeyboardVisible) {
                                    Image(Icons.Filled.Clear,
                                        contentDescription = "X Icon",
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clickable {
                                                // Hide the keyboard
                                                focusManager.clearFocus()
                                                // Clear the text field
                                                viewModel.onSearchKeywordChanged("")
                                                // Reset the search results
                                                viewModel.performSearch("")
                                            })
                                }
                            },
                            shape = RoundedCornerShape(18.dp),
                            colors = textFieldColors,
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(
                        modifier = Modifier
                            .verticalScroll(scrollState)
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.find_friend),
                                contentDescription = "Find Friend Icon",
                                modifier = Modifier.size(25.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Tìm bạn bè từ ứng dụng khác", style = TextStyle(
                                    fontSize = 20.sp,
                                    fontFamily = fontFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF959292),
                                    textAlign = TextAlign.Center,
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .background(
                                    color = Color(0xFF141414),
                                    shape = RoundedCornerShape(size = 15.dp)
                                )
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                BasicShareButton(
                                    drawableResource = R.drawable.icon_messenger,
                                    action = { /*TODO*/ },
                                    description = "Messenger Icon",
                                    text = "Messenger"
                                )
                                BasicShareButton(
                                    drawableResource = R.drawable.icon_instagram,
                                    action = { /*TODO*/ },
                                    description = "Instagram Icon",
                                    text = "Instagram"
                                )
                                BasicShareButton(
                                    drawableResource = R.drawable.icon_message,
                                    action = { /*TODO*/ },
                                    description = "Message Icon",
                                    text = "Tin nhắn"
                                )
                                BasicShareButton(
                                    drawableResource = R.drawable.icon_other,
                                    action = { /*TODO*/ },
                                    description = "Other Icon",
                                    text = "Khác"
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.icon_people),
                                contentDescription = "People Icon",
                                modifier = Modifier.size(25.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = " Bạn bè của bạn", style = TextStyle(
                                    fontSize = 20.sp,
                                    fontFamily = fontFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF959292),
                                )
                            )
                        }
                        Column(
                            // Replace LazyColumn with Column
                            modifier = Modifier.fillMaxWidth(), // Change to fillMaxWidth
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top,
                        ) {
                            state.data.forEach { user ->
                                FriendItem(user = user, action = { /*TODO*/ })
                            }
                        }
                    }
                    // Next Button
                    Spacer(modifier = Modifier.height(16.dp))
                    Column {
                        val buttonColor = ButtonDefaults.buttonColors(
                            YellowPrimary
                        )
                        Button(
                            onClick = {
                                viewModel.onContinueClick(clearAndNavigate)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            colors = buttonColor
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {

                                Text(
                                    text = "Tiếp tục", style = TextStyle(
                                        fontSize = 24.sp,
                                        fontFamily = fontFamily,
                                        color = BlackSecondary,
                                        fontWeight = FontWeight.Bold
                                    ), modifier = Modifier.align(Alignment.CenterVertically)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Image(
                                    painter = painterResource(id = R.drawable.arrow_right),
                                    contentDescription = "image description",
                                    modifier = Modifier
                                        .size(24.dp)
                                        .align(Alignment.CenterVertically)
                                )
                            }
                        }
                    }
                }
            }
        }

        is DataState.Error -> ErrorScreen(errorMessage = "Lỗi xảy ra khi lấy danh sách bạn bè", onRetry = {
            viewModel.fetchSuggestFriendList()
        })
    }
}