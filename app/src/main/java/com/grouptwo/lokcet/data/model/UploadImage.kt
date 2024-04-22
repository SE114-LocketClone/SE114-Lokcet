package com.grouptwo.lokcet.data.model

import java.time.LocalDateTime

data class EmojiReaction(
    val emojiId: String = "",
    val userId: String = "",
    val imageId: String = "",
    val createdAt: Any = Any(),
)

data class UploadImage(
    val imageId: String = "",
    val userId: String = "",
    val imageUrl: String = "",
    val imageCaption: String = "",
    val imageLocation: String = "",
    val visibleUserIds: List<String>? = null,
    val createdAt: Any = Any(),
)