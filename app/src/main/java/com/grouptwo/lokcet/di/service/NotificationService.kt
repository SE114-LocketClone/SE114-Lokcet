package com.grouptwo.lokcet.di.service

import com.grouptwo.lokcet.data.model.NotificationModel
import com.grouptwo.lokcet.utils.Constants
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationService {
    companion object {
        const val BASE_URL = "https://fcm.googleapis.com"
        private const val CONTENT_TYPE = "application/json"
    }

    @POST("/fcm/send")
    @Headers(
        "Authorization: key=${Constants.FIREBASE_NOTIFICATION_API_KEY}",
        "Content-Type:$CONTENT_TYPE"
    )
    suspend fun postNotification(
        @Body notification: NotificationModel
    ): Response<ResponseBody>
}
