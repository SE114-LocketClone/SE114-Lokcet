package com.grouptwo.lokcet.data.model

import android.database.DatabaseErrorHandler
import com.google.firebase.Timestamp
import com.google.type.DateTime
import java.time.LocalDateTime

data class UploadImage (
    val imageId: String = "",
    val userId: String = "",
    val imageUrl: String = "",
    val imageCaption: String = "",
    val imageLocation: String = "",
    val createdAt: LocalDateTime = LocalDateTime.now(),
)