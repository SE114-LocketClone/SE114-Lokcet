package com.grouptwo.lokcet.di.impl

import android.util.Log
import com.grouptwo.lokcet.data.model.NotificationModel
import com.grouptwo.lokcet.di.service.NotificationService
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class NotificationServiceRepository @Inject constructor(
    private val retrofit: NotificationService
) {

    suspend fun postNotification(notification: NotificationModel): Response<ResponseBody> {
        Log.d("NotificationServiceRepository", "postNotification: $notification")
        val response = retrofit.postNotification(notification)
        Log.d("NotificationServiceRepository", "postNotification: $response")
        return response
    }
}