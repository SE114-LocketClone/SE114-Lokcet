package com.grouptwo.lokcet

import android.annotation.SuppressLint
import android.content.res.Resources
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.grouptwo.lokcet.navigation.Screen
import com.grouptwo.lokcet.ui.component.global.snackbar.SnackbarManager
import com.grouptwo.lokcet.ui.theme.BlackSecondary
import com.grouptwo.lokcet.ui.theme.LokcetTheme
import com.grouptwo.lokcet.ui.theme.YellowPrimary
import com.grouptwo.lokcet.view.add_friend.AddFriendScreen
import com.grouptwo.lokcet.view.add_widget.AddWidgetScreen
import com.grouptwo.lokcet.view.feed.FeedScreen
import com.grouptwo.lokcet.view.friend.FriendScreen
import com.grouptwo.lokcet.view.home.HomeScreen1
import com.grouptwo.lokcet.view.home.HomeScreen2
import com.grouptwo.lokcet.view.home.HomeViewModel
import com.grouptwo.lokcet.view.login.LoginScreen1
import com.grouptwo.lokcet.view.login.LoginScreen2
import com.grouptwo.lokcet.view.login.LoginViewModel
import com.grouptwo.lokcet.view.register.RegisterScreen1
import com.grouptwo.lokcet.view.register.RegisterScreen2
import com.grouptwo.lokcet.view.register.RegisterScreen3
import com.grouptwo.lokcet.view.register.RegisterScreen4
import com.grouptwo.lokcet.view.register.RegisterViewModel
import com.grouptwo.lokcet.view.splash.SplashScreen
import com.grouptwo.lokcet.view.welcome.WelcomeScreen
import kotlinx.coroutines.CoroutineScope

@Composable
fun LokcetApp() {
    LokcetTheme {
        Surface {
            val appState = rememberAppState()
            Scaffold(
                containerColor = BlackSecondary,
                snackbarHost = {
                    SnackbarHost(hostState = appState.snackbarHostState,
                        modifier = Modifier
                            .statusBarsPadding()
                            .navigationBarsPadding()
                            .imePadding()
                            .padding(8.dp),
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
) = remember(snackbarHostState, navController, snackbarManager, resources, coroutineScope) {
    LokcetAppState(snackbarHostState, navController, snackbarManager, resources, coroutineScope)
}

@Composable
@ReadOnlyComposable
fun resources(): Resources {
    LocalConfiguration.current
    return LocalContext.current.resources
}


@SuppressLint("UnrememberedGetBackStackEntry")
fun NavGraphBuilder.LokcetGraph(appState: LokcetAppState) {
    // Splash screen
    composable(Screen.SplashScreen.route) {
        SplashScreen(openAndPopUp = { route, popUp ->
            appState.navigateAndPopUp(route, popUp)
        })
    }
    // Welcome screen
    composable(Screen.WelcomeScreen.route) {
        WelcomeScreen(navigate = { route ->
            appState.navigate(route)
        })
    }
    // Add widget screen
    composable(Screen.AddWidgetTutorialScreen.route) {
        AddWidgetScreen(clearAndNavigate = { route ->
            appState.clearAndNavigate(route)
        })
    }
    // Register screens
    composable(Screen.RegisterScreen_1.route) {
        RegisterScreen1(popUp = { appState.popUp() }, navigate = { route ->
            appState.navigate(route)
        })
    }
    composable(Screen.RegisterScreen_2.route) { backStackEntry ->
        // Share parent viewmodel with given route
        val parentEntry = remember(backStackEntry) {
            appState.navController.getBackStackEntry(Screen.RegisterScreen_1.route)
        }
        val vm = hiltViewModel<RegisterViewModel>(parentEntry)
        RegisterScreen2(popUp = { appState.popUp() }, navigate = { route ->
            appState.navigate(route)
        }, viewModel = vm)
    }
    composable(Screen.RegisterScreen_3.route) { backStackEntry ->
        // Share parent viewmodel with given route
        val parentEntry = remember(backStackEntry) {
            appState.navController.getBackStackEntry(Screen.RegisterScreen_1.route)
        }
        val vm = hiltViewModel<RegisterViewModel>(parentEntry)
        RegisterScreen3(popUp = { appState.popUp() }, viewModel = vm, navigate = { route ->
            appState.navigate(route)
        })
    }
    composable(Screen.RegisterScreen_4.route) { backStackEntry ->
        // Share parent viewmodel with given route
        val parentEntry = remember(backStackEntry) {
            appState.navController.getBackStackEntry(Screen.RegisterScreen_1.route)
        }
        val vm = hiltViewModel<RegisterViewModel>(parentEntry)
        RegisterScreen4(popUp = { appState.popUp() }, viewModel = vm, clearAndNavigate = { route ->
            appState.clearAndNavigate(route)
        })
    }

    // Login screen
    composable(Screen.LoginScreen_1.route) {
        LoginScreen1(popUp = { appState.popUp() }, navigate = { route -> appState.navigate(route) })
    }

    composable(Screen.LoginScreen_2.route) { backStackEntry ->
        val parentEntry = remember(backStackEntry) {
            appState.navController.getBackStackEntry(Screen.LoginScreen_1.route)
        }
        val vm = hiltViewModel<LoginViewModel>(parentEntry)
        LoginScreen2(popUp = { appState.popUp() },
            clearAndNavigate = { route -> appState.clearAndNavigate(route) },
            viewModel = vm
        )
    }

    // Add friend screen
    composable(Screen.AddFriendScreen.route) {
        AddFriendScreen(clearAndNavigate = { route ->
            appState.clearAndNavigate(route)
        })
    }
    // Home screens
    composable(Screen.HomeScreen_1.route) {
        HomeScreen1(navigate = { route ->
            appState.navigate(route)
        })
    }
    composable(Screen.HomeScreen_2.route) { backStackEntry ->
        // Share parent viewmodel with given route
        val parentEntry = remember(backStackEntry) {
            appState.navController.getBackStackEntry(Screen.HomeScreen_1.route)
        }
        val vm = hiltViewModel<HomeViewModel>(parentEntry)
        HomeScreen2(
            clearAndNavigate = { route ->
                appState.clearAndNavigate(route)
            }, viewModel = vm
        )
    }
    // Feed screen
    composable(Screen.FeedScreen.route) {
        FeedScreen()
    }
    // Friend screen
    composable(Screen.FriendScreen.route) {
        FriendScreen(clearAndNavigate = { route ->
            appState.clearAndNavigate(route)
        })
    }
}

