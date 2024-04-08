package com.grouptwo.lokcet

import android.content.res.Resources
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.grouptwo.lokcet.ui.theme.LokcetTheme
import com.grouptwo.lokcet.navigation.Screen
import com.grouptwo.lokcet.ui.component.global.composable.PermissionDialog
import com.grouptwo.lokcet.ui.component.global.composable.RationaleDialog
import com.grouptwo.lokcet.ui.component.global.snackbar.SnackbarManager
import com.grouptwo.lokcet.ui.theme.BlackSecondary
import com.grouptwo.lokcet.ui.theme.YellowPrimary
import com.grouptwo.lokcet.view.splash.SplashScreen
import com.grouptwo.lokcet.view.welcome.WelcomeScreen
import kotlinx.coroutines.CoroutineScope
import android.Manifest

@Composable
fun LokcetApp() {
    LokcetTheme {
        // Check if use Android 13 - Tiramisu then must request notification permission
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            // Request notification permission
            RequestNotificationPermission()
        }
        Surface(
            color = BlackSecondary,
            modifier = Modifier.fillMaxSize()
        ) {
            val appState = rememberAppState()

            Scaffold(
                containerColor = BlackSecondary,
                snackbarHost = {
                    SnackbarHost(
                        hostState = appState.snackbarHostState,
                        modifier = Modifier.padding(8.dp),
                        snackbar = { snackbarData ->
                            Snackbar(snackbarData = snackbarData, contentColor = YellowPrimary)
                        })
                },
            ) { innerPaddingModifier ->
                NavHost(
                    navController = appState.navController,
                    startDestination = Screen.SplashScreen.route,
                    modifier = Modifier.padding(innerPaddingModifier)
                ) {
                    LokcetGraph(appState)
                }
            }

        }

    }
}

@Composable
fun rememberAppState(
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    navController: NavHostController = rememberNavController(),
    snackbarManager: SnackbarManager = SnackbarManager,
    resources: Resources = resources(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) =
    remember(snackbarHostState, navController, snackbarManager, resources, coroutineScope) {
        LokcetAppState(snackbarHostState, navController, snackbarManager, resources, coroutineScope)
    }

@Composable
@ReadOnlyComposable
fun resources(): Resources {
    LocalConfiguration.current
    return LocalContext.current.resources
}



fun NavGraphBuilder.LokcetGraph(appState: LokcetAppState) {
    composable(Screen.SplashScreen.route){
       SplashScreen(appState.navController)
    }
    composable(Screen.WelcomeScreen.route) {
        WelcomeScreen()
    }

}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestNotificationPermission() {
    // Request notification permission
    val permissionState = rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)

    if (!permissionState.status.isGranted) {
        if (permissionState.status.shouldShowRationale) RationaleDialog()
        else PermissionDialog { permissionState.launchPermissionRequest() }
    }
}