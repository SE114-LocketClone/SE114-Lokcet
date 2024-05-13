package com.grouptwo.lokcet

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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
        lifecycleScope.launch {
            Log.d("LokcetActivity", "onCreate: Starting")
            getAndStoreRegToken()
            Log.d("LokcetActivity", "onCreate: Finished")
        }
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

    private suspend fun getAndStoreRegToken() {
        // [START log_reg_token]
        var token = Firebase.messaging.token.await()
        // Check whether the retrieved token matches the one on your server for this user's device
        val preferences = this.getSharedPreferences("local_shared_pref", Context.MODE_PRIVATE)
        val tokenStored = preferences.getString("deviceToken", "")
        val userId = preferences.getString("userId", "")
        Log.d("LokcetActivity", "user ID: $userId")
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
                    Log.d("LokcetActivity", "Token stored on firestore: $token")
                    preferences.edit().putString("deviceToken", token).apply()
                }
            }
        }
    }
}

