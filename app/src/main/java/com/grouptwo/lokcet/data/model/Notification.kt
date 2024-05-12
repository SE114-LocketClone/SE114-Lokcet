package com.grouptwo.lokcet.data.model

data class NotificationDataModel(
    val title: String,
    val message: String
)

data class NotificationModel(
    val data: NotificationDataModel,
    val to: String
)