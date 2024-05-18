package com.grouptwo.lokcet

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.compose.material.ExperimentalMaterialApi
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.messaging
import com.grouptwo.lokcet.data.model.FCMToken
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import kotlin.random.Random

@AndroidEntryPoint
class LokcetMessagingService(
) : FirebaseMessagingService() {

    override fun onCreate() {
        super.onCreate()
        Firebase.messaging.isAutoInitEnabled = true
    }

    private val random = Random

    override fun onNewToken(token: String) {
        // Store new token
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val imageUrl = remoteMessage.data["image"] ?: ""
        remoteMessage.notification?.let { message ->
            sendNotification(message, imageUrl)
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    private fun sendNotification(message: RemoteMessage.Notification, imageUrl: String? = null) {
        // If you want the notifications to appear when your app is in foreground

        val intent = Intent(this, LokcetActivity::class.java).apply {
            addFlags(FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
        )

        val channelId = this.getString(R.string.default_notification_channel_id)

        val notificationBuilder =
            NotificationCompat.Builder(this, channelId).setContentTitle(message.title)
                .setContentText(message.body).setSmallIcon(R.mipmap.ic_launcher).setAutoCancel(true)
                .setContentIntent(pendingIntent)

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, CHANNEL_NAME, IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }

        CoroutineScope(Dispatchers.IO).launch {
            if (!imageUrl.isNullOrEmpty()) {
                val bitmap = downloadImage(imageUrl)
                if (bitmap != null) {
                    notificationBuilder.setLargeIcon(bitmap)
                    notificationBuilder.setStyle(
                        NotificationCompat.BigPictureStyle().bigPicture(bitmap)
                    )
                }
            }
            manager.notify(random.nextInt(), notificationBuilder.build())
        }
    }


    companion object {
        const val CHANNEL_NAME = "FCM notification channel"
    }

    private suspend fun downloadImage(imageUrl: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(imageUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val input = connection.inputStream
                BitmapFactory.decodeStream(input)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

    }
}

class UpdateTokenWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        // Refresh the token and send it to your server
        var token = Firebase.messaging.token.await()
        val preferences =
            applicationContext.getSharedPreferences("local_shared_pref", Context.MODE_PRIVATE)
        val userId = preferences.getString("userId", "")
        if (userId == "") return Result.failure()
        Firebase.firestore.collection("fcmTokens").document(userId!!)
            .set(FCMToken(token = token)).await()
        preferences.edit().putString("deviceToken", token).apply()
        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }
}
