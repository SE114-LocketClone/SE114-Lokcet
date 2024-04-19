package com.grouptwo.lokcet.view.welcome

import android.Manifest
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.grouptwo.lokcet.ui.component.global.composable.BasicTextButton
import com.grouptwo.lokcet.ui.component.global.permission.RequestCameraPermission
import com.grouptwo.lokcet.ui.component.global.permission.RequestContactPermission
import com.grouptwo.lokcet.ui.component.global.permission.RequestLocationPermission
import com.grouptwo.lokcet.ui.component.global.permission.RequestNotificationPermission
import com.grouptwo.lokcet.ui.component.global.permission.RequestStoragePermission
import com.grouptwo.lokcet.ui.component.welcome.AutoScrollImage
import com.grouptwo.lokcet.ui.theme.BlackSecondary
import com.grouptwo.lokcet.ui.theme.fontFamily
import com.grouptwo.lokcet.R.string as WelcomeString

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun WelcomeScreen(
    navigate: (String) -> Unit, viewModel: WelcomeViewModel = hiltViewModel()
) {
    // Ask for permissions
    val locationPermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    val contactPermissionState =
        rememberPermissionState(permission = Manifest.permission.READ_CONTACTS)

    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

    // If user is on Android 13 or higher, ask for notification permission as well
    // Using if else statement to show the permission dialog sequentially
    // Not call directly to avoid multiple permission dialog at the same time
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val notificationPermissionState =
            rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
        if (notificationPermissionState.status.isGranted.not()) {
            RequestNotificationPermission()
        }
        if (locationPermissionState.allPermissionsGranted.not()) {
            RequestLocationPermission()
        }
        if (contactPermissionState.status.isGranted.not()) {
            RequestContactPermission()
        }
        if (cameraPermissionState.status.isGranted.not()) {
            RequestCameraPermission()
        }
    } else {
        val storagePermissionState = rememberMultiplePermissionsState(
            permissions = listOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        )
        // If user is on Android 12 or lower, ask for location and contact permission
        if (locationPermissionState.allPermissionsGranted.not()) {
            RequestLocationPermission()
        }
        if (contactPermissionState.status.isGranted.not()) {
            RequestContactPermission()
        }
        if (cameraPermissionState.status.isGranted.not()) {
            RequestCameraPermission()
        }
        if (storagePermissionState.allPermissionsGranted.not()) {
            RequestStoragePermission()
        }
    }
    // Welcome Screen
    // Display the welcome screen
    val images = listOf(
        com.grouptwo.lokcet.R.drawable.miniphone_1,
        com.grouptwo.lokcet.R.drawable.miniphone_2,
    )
    // Fill the screen with the welcome screen content
    Box(modifier = Modifier.fillMaxSize()) {
        // Display the welcome
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)

        ) {
            Spacer(modifier = Modifier.height(30.dp))
            // Auto Scroll Image
            AutoScrollImage(images = images, duration = 3000L)
            // Logo Icon and Logo Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 60.dp, bottom = 16.dp)
            ) {
                Image(
                    painter = painterResource(id = com.grouptwo.lokcet.R.drawable.app_logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(50.dp)
                )
                Text(
                    text = stringResource(id = WelcomeString.app_name), style = TextStyle(
                        color = androidx.compose.ui.graphics.Color.White,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = fontFamily
                    ), modifier = Modifier.padding(start = 8.dp)
                )
            }
            Text(
                text = stringResource(id = WelcomeString.welcome_text),
                style = TextStyle(
                    fontSize = 20.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB8B8B8),
                    textAlign = TextAlign.Center,
                ),
                maxLines = 2,
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 32.dp)
                    .fillMaxWidth()
            )

            // Create Account Button
            BasicTextButton(
                stringResource = WelcomeString.create_account,
                modifier = Modifier
                    .width(300.dp)
                    .padding(8.dp)
                    .clip(
                        shape = RoundedCornerShape(50)
                    ),
                action = { viewModel.onRegisterClick(navigate) },
                textStyle = TextStyle(
                    fontSize = 20.sp,
                    fontFamily = fontFamily,
                    color = BlackSecondary,
                    fontWeight = FontWeight.Bold
                ),
            )
            // Login Touchable
            Surface(color = Color.Transparent, modifier = Modifier
                .clickable {
                    viewModel.onLoginClick(navigate)
                }
                .padding(top = 20.dp)
                .fillMaxWidth()) {
                Text(
                    text = stringResource(id = WelcomeString.login), style = TextStyle(
                        fontSize = 20.sp,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8D8D8D),
                        textAlign = TextAlign.Center,
                    )
                )
            }
        }
    }
}
