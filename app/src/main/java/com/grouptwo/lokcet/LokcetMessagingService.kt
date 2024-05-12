package com.grouptwo.lokcet

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.os.Build
import androidx.compose.material.ExperimentalMaterialApi
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.messaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

class LokcetMessagingService : FirebaseMessagingService() {
    private val random = Random

    suspend fun storeToken(token: String) {
        val device_token = hashMapOf(
            "token" to token,
            "timestamp" to FieldValue.serverTimestamp(),
        )
        // Get user ID from shared preferences
        val sharedPreferences = this.getSharedPreferences("local_shared_pref", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", "")
        // Make sure user is logged in before storing token
        if (userId == "") return
        Firebase.firestore.collection("fcmTokens").document(userId!!)
            .set(device_token).await()
        // Store token in shared preferences
        val editor = sharedPreferences.edit().putString("deviceToken", token).apply()
    }

    suspend fun getAndRegToken() {
        val preferences = this.getSharedPreferences("local_shared_pref", Context.MODE_PRIVATE)
        val tokenStored = preferences.getString("deviceToken", "")
        CoroutineScope(Dispatchers.IO).launch {
            val token = Firebase.messaging.token.await()
            if (tokenStored == "" || tokenStored != token) {
                storeToken(token)
            }
        }
    }

    override fun onNewToken(token: String) {
        // Store new token
        CoroutineScope(Dispatchers.IO).launch {
            storeToken(token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.notification?.let { message ->
            sendNotification(message)
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    private fun sendNotification(message: RemoteMessage.Notification) {
        // If you want the notifications to appear when your app is in foreground

        val intent = Intent(this, LokcetActivity::class.java).apply {
            addFlags(FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, FLAG_IMMUTABLE
        )

        val channelId = this.getString(R.string.default_notification_channel_id)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(message.title)
            .setContentText(message.body)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, CHANNEL_NAME, IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }

        manager.notify(random.nextInt(), notificationBuilder.build())
    }

    companion object {
        const val CHANNEL_NAME = "FCM notification channel"
    }


}

class UpdateTokenWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        // Refresh the token and send it to your server
        var token = Firebase.messaging.token.await()
        LokcetMessagingService().storeToken(token)

        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }
}