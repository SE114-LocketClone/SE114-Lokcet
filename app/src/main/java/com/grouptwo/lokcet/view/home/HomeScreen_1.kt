package com.grouptwo.lokcet.view.home

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.grouptwo.lokcet.R
import com.grouptwo.lokcet.ui.theme.fontFamily
import com.grouptwo.lokcet.utils.noRippleClickable
import com.yalantis.ucrop.UCrop


@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen1(
    viewModel: HomeViewModel = hiltViewModel(), navigate: (String) -> Unit
) {
    val activity = LocalContext.current as Activity
    val cropResultLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val uri = UCrop.getOutput(result.data!!)
                // Handle the cropped image URI
                viewModel.onImagePicked(uri, navigate)
            }
        }
    val uiState by viewModel.uiState.collectAsState()
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) {

            if (it != null) {
                viewModel.startCropping(activity, it, cropResultLauncher)
            }
        }
    // Display the home scree
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
                Image(painter = painterResource(id = R.drawable.icon_friend),
                    contentDescription = "User Logo",
                    colorFilter = ColorFilter.tint(Color(0xFF272626)),
                    modifier = Modifier
                        .size(50.dp)
                        .noRippleClickable {
                            // Navigate to the user profile screen
                        })
                Button(
                    onClick = {
                        viewModel.onFriendButtonClick(navigate)
                    },
                    modifier = Modifier.wrapContentSize(),
                    colors = ButtonDefaults.buttonColors(Color(0xFF272626)),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.icon_people),
                            contentDescription = "Friend Logo",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(
                            text = "Bạn bè", style = TextStyle(
                                color = Color.White,
                                fontFamily = fontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        )
                    }
                }
                IconButton(
                    onClick = {
                        viewModel.onChatButtonClick(navigate)
                    }, colors = IconButtonDefaults.iconButtonColors(
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
            CameraView(lensFacing = uiState.lensFacing, onImageCapture = { image ->
                viewModel.onImageCaptured(image, navigate)
            }, modifier = Modifier.weight(0.65f), onSwitchCamera = {
                viewModel.switchCamera()
            }, launcher = launcher)
            Spacer(modifier = Modifier.weight(0.05f))
            Column(horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.noRippleClickable {
                    // Navigate to the feed screen
                    viewModel.onSwipeUp(navigate)
                }) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.feed),
                        contentDescription = "Friend Logo",
                        modifier = Modifier.size(25.dp),
                        colorFilter = ColorFilter.tint(Color(0xFF8A8D8E))
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "Feed", style = TextStyle(
                            color = Color.White,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Image(
                    painter = painterResource(id = R.drawable.arrow_down),
                    contentDescription = "Arrow Down",
                    modifier = Modifier.size(32.dp),
                    colorFilter = ColorFilter.tint(Color(0xFF8A8D8E))
                )
            }
        }
    }
}
