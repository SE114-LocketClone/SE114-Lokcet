package com.grouptwo.lokcet.data.model

import android.database.DatabaseErrorHandler
import com.google.firebase.Timestamp
import com.google.type.DateTime

data class UploadImage (
    val imageUrl: String = "",
    val imageName: String = "",
    val imageDescription: String = "",
    val imageLocation: String = "",
    val createdAt: Timestamp = Timestamp.now(),
)