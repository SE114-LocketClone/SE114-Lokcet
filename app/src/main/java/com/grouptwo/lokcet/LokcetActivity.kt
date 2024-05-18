package com.grouptwo.lokcet

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.Firebase
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.messaging
import com.grouptwo.lokcet.data.model.FCMToken
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class LokcetActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(this.window, false)
        super.onCreate(savedInstanceState)
        // Place this code on top to immediately get the deep link and show the correct screen
//        FirebaseDynamicLinks.getInstance()
//            .getDynamicLink(intent)
//            .addOnSuccessListener(this) { pendingDynamicLinkData ->
//                val deepLink: Uri? = pendingDynamicLinkData?.link
//                setContent {
//                    LokcetApp(deepLink)
//                }
//            }
//            .addOnFailureListener(this) { e ->
//                Log.w(
//                    "LokcetActivity",
//                    "getDynamicLink:onFailure",
//                    e
//                )
//                // On failure, set deepLink to null
//                setContent {
//                    LokcetApp(null)
//                }
//            }
        val deepLinkState =  mutableStateOf<Uri?>(null)

        setContent {
            LokcetApp(deepLinkState.value)
            // Reset deeplink after use
            deepLink.value = null
        }

        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                // Update the deepLinkState with the fetched deep link
                deepLinkState.value = pendingDynamicLinkData?.link
            }
        val saveRequest =
            PeriodicWorkRequestBuilder<UpdateTokenWorker>(730, TimeUnit.HOURS)
                .build()
        // Enqueue the work request
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "saveRequest", ExistingPeriodicWorkPolicy.UPDATE, saveRequest
        )
        lifecycleScope.launch(Dispatchers.IO) {
            getAndStoreRegToken()
        }
    }

    private suspend fun getAndStoreRegToken() {
        // [START log_reg_token]
        var token = Firebase.messaging.token.await()
        // Check whether the retrieved token matches the one on your server for this user's device
        val preferences = this.getSharedPreferences("local_shared_pref", Context.MODE_PRIVATE)
        val tokenStored = preferences.getString("deviceToken", "")
        val userId = preferences.getString("userId", "")
        lifecycleScope.launch {
            if (tokenStored == "" || tokenStored != token) {
                // If you have your own server, call API to send the above token and Date() for this user's device
                // Example shown below with Firestore
                // Add token and timestamp to Firestore for this user
                val deviceToken = FCMToken(token = token)
                // Get user ID from Firebase Auth or your own server
                if (!userId.isNullOrEmpty()) {
                    Firebase.firestore.collection("fcmTokens").document(userId)
                        .set(deviceToken).await()
                    preferences.edit().putString("deviceToken", token).apply()
                }
            }
        }
    }
}

