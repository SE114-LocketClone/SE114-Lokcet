package com.grouptwo.lokcet.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.time.LocalDateTime
import java.util.Date

data class EmojiReaction(
    val emojiId: String = "",
    val userId: String = "",
    val imageId: String = "",
    @ServerTimestamp    val createdAt: Date = Date(),
)

data class UploadImage(
    val imageId: String = "",
    val userId: String = "",
    val imageUrl: String = "",
    val imageCaption: String = "",
    val imageLocation: String = "",
    val visibleUserIds: List<String>? = null,
    @ServerTimestamp val createdAt: Date = Date(),
)