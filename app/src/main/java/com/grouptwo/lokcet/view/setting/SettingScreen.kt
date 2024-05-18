package com.grouptwo.lokcet.view.setting

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import com.grouptwo.lokcet.ui.theme.BlackSecondary
import com.grouptwo.lokcet.ui.theme.YellowPrimary
import com.grouptwo.lokcet.ui.theme.fontFamily
import com.grouptwo.lokcet.utils.noRippleClickable
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import com.yalantis.ucrop.UCrop

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    viewModel: SettingViewModel = hiltViewModel(), navigate: (String) -> Unit, popUp: () -> Unit
) {
    val activity = LocalContext.current as Activity
    val scrollState = rememberScrollState()
    val imeState = rememberImeState()
    val uiState = viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )

    LaunchedEffect(key1 = imeState.value) {
        if (imeState.value) {
            scrollState.animateScrollTo(scrollState.maxValue, tween(300))
        }
    }
    val cropResultLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val uri = UCrop.getOutput(result.data!!)
                // Handle the cropped image URI
                viewModel.onImagePicked(uri)
            }
        }


    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) {
            if (it != null) {
                viewModel.startCropping(activity, it, cropResultLauncher)
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
                    action = {
                        viewModel.onBackClick(popUp)
                    },
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
                                width = 3.dp, color = Color(0xFFE5A500), shape = CircleShape
                            )
                            .clickable {
                                // Open bottom sheet to change avatar
                                viewModel.onClickAvatarBottomDialog(true)
                            }, contentAlignment = Alignment.Center
                    ) {
                        if (uiState.value.isImageUpload) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(20.dp)
                                    .align(
                                        Alignment.Center
                                    ), color = YellowPrimary
                            )
                        } else {
                            GlideImage(
                                imageModel = {
                                    uiState.value.avatarUrl ?: ""
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
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .noRippleClickable {
                                // Open new screen to change name
                                viewModel.onClickChangeName(navigate)
                            }
                            .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween) {
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
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .noRippleClickable {
                                // Open new screen to change phone
                                viewModel.onClickChangePhone(navigate)
                            }
                            .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween) {
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
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .noRippleClickable {
                                // Open new screen to report a problem
                                viewModel.onClickReportProblem(navigate)
                            }
                            .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween) {
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
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .noRippleClickable {
                                // Open new screen to make a suggestion
                                viewModel.onClickMakeSuggestion(navigate)
                            }
                            .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween) {
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
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .noRippleClickable {
                                // Open new screen to delete account
                                viewModel.onShowDeleteAccountDialog(true)
                            }
                            .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween) {
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
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .noRippleClickable {
                                // Open new screen to log out
                                viewModel.onShowLogoutDialog(true)
                            }
                            .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween) {
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
        // Delete account dialog
        if (uiState.value.isShowDeleteAccountDialog) {
            // Show delete account dialog
            AlertDialog(
                onDismissRequest = {
                    // Dismiss the dialog when the user presses back button or touches outside.
                    viewModel.onShowDeleteAccountDialog(false)
                },
                containerColor = BlackSecondary,
                title = {
                    Text(
                        "Xác nhận xóa tài khoản", style = TextStyle(
                            color = Color.White,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    )
                },
                text = {
                    Text(
                        "Bạn có chắc chắn muốn tài khoản này?\nNếu xóa, tài khoản sẽ không thể khôi phục lại.",
                        style = TextStyle(
                            color = Color.White,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            // Delete the feed and then dismiss the dia
                            viewModel.onClickDeleteAccount(navigate)
                        },
                        colors = ButtonDefaults.buttonColors(YellowPrimary),
                        shape = RoundedCornerShape(50.dp),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(
                            "Xóa", style = TextStyle(
                                color = Color.White,
                                fontFamily = fontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        )
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            // Dismiss the dialog when the user presses back button or touches outside.
                            viewModel.onShowDeleteAccountDialog(false)
                        },
                        colors = ButtonDefaults.buttonColors(Color(0xFF272626)),
                        shape = RoundedCornerShape(50.dp),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(
                            "Hủy bỏ", style = TextStyle(
                                color = Color.White,
                                fontFamily = fontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        )
                    }
                },
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .clip(
                        RoundedCornerShape(50.dp)
                    ),
            )
        }
        // Logout dialog
        if (uiState.value.isShowLogoutDialog) {
            // Show logout dialog
            AlertDialog(
                onDismissRequest = {
                    // Dismiss the dialog when the user presses back button or touches outside.
                    viewModel.onShowLogoutDialog(false)
                },
                containerColor = BlackSecondary,
                title = {
                    Text(
                        "Xác nhận đăng xuất", style = TextStyle(
                            color = Color.White,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    )
                },
                text = {
                    Text(
                        "Bạn có chắc chắn muốn đăng xuất khỏi tài khoản này?", style = TextStyle(
                            color = Color.White,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            // Logout the feed and then dismiss the dialog.
                            viewModel.onClickLogout(navigate)
                        },
                        colors = ButtonDefaults.buttonColors(YellowPrimary),
                        shape = RoundedCornerShape(50.dp),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(
                            "Đăng xuất", style = TextStyle(
                                color = Color.White,
                                fontFamily = fontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        )
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            // Dismiss the dialog when the user presses back button or touches outside.
                            viewModel.onShowLogoutDialog(false)
                        },
                        colors = ButtonDefaults.buttonColors(Color(0xFF272626)),
                        shape = RoundedCornerShape(50.dp),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(
                            "Hủy bỏ", style = TextStyle(
                                color = Color.White,
                                fontFamily = fontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        )
                    }
                },
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .clip(
                        RoundedCornerShape(50.dp)
                    ),
            )
        }
        if (uiState.value.isShowAvatarBottomDialog) {
            ModalBottomSheet(
                sheetState = sheetState,
                shape = RoundedCornerShape(20.dp),
                tonalElevation = 0.dp,
                onDismissRequest = {
                    viewModel.onClickAvatarBottomDialog(false)
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
                        .heightIn(max = 400.dp)
                ) {
                    Column(
                        modifier = Modifier.wrapContentSize()
                    ) {
                        Button(
                            onClick = {
                                launcher.launch(
                                    PickVisualMediaRequest(
                                        mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            }, colors = ButtonDefaults.buttonColors(
                                backgroundColor = BlackSecondary, contentColor = Color.White
                            ), modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Chọn ảnh từ thư viện", style = TextStyle(
                                    fontSize = 16.sp,
                                    fontFamily = fontFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )
                            )
                        }
                        Button(
                            onClick = {
                                viewModel.onRemoveImage()
                            }, colors = ButtonDefaults.buttonColors(
                                backgroundColor = BlackSecondary, contentColor = Color.White
                            ), modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Xóa ảnh đại diện", style = TextStyle(
                                    fontSize = 16.sp,
                                    fontFamily = fontFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Red,
                                    textAlign = TextAlign.Center
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

