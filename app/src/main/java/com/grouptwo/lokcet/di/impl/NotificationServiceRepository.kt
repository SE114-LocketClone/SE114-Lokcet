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
        val response = retrofit.postNotification(notification)
        return response
    }
}