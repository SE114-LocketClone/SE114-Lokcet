package com.grouptwo.lokcet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.grouptwo.lokcet.view.Addfriend.AddfriendScreen
import com.grouptwo.lokcet.view.Addfriend.AddfriendScreen1
import com.grouptwo.lokcet.view.Addfriend.AddfriendScreen2
import com.grouptwo.lokcet.view.Addfriend.AddfriendScreen3
import androidx.core.view.WindowCompat
import com.grouptwo.lokcet.view.setting.SettingScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LokcetActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            /*LokcetTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BlackSecondary
                ) {
                    LokcetApp()
                }
            }*/


            // A surface container using the 'background' color from the theme
            //LokcetApp()
            SettingScreen()
        }
    }
}
