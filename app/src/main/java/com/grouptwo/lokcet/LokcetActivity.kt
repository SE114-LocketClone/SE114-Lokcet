package com.grouptwo.lokcet

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class LokcetActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(this.window, false)
        super.onCreate(savedInstanceState)
        val saveRequest =
            PeriodicWorkRequestBuilder<UpdateTokenWorker>(730, TimeUnit.HOURS)
                .build()
        // Enqueue the work request
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "saveRequest", ExistingPeriodicWorkPolicy.UPDATE, saveRequest
        )
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                val deepLink: Uri? = pendingDynamicLinkData?.link
                setContent {
                    LokcetApp(deepLink)
                }
            }
            .addOnFailureListener(this) { e ->
                Log.w(
                    "LokcetActivity",
                    "getDynamicLink:onFailure",
                    e
                )
                // On failure, set deepLink to null
                setContent {
                    LokcetApp(null)
                }
            }
    }
}

