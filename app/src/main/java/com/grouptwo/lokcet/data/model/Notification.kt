package com.grouptwo.lokcet.data.model

data class NotificationNotiModel(
    val title: String,
    val body: String,
)

data class NotificationDataModel(
    val message: String,
    val image: String, // image url
)

data class NotificationModel(
    val notification: NotificationNotiModel,
    val to: String,
    val data: NotificationDataModel
)