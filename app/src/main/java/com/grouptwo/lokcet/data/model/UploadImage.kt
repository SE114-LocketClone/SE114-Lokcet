package com.grouptwo.lokcet.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class EmojiReaction(
    val emojiId: String = "",
    val userId: String = "",
    val imageId: String = "",
    @ServerTimestamp val createdAt: Date = Date(),
)

data class UploadImage(
    val imageId: String = "",
    val userId: String = "",
    val userName: String = "", // This is the user's display name at the time of the upload
    val imageUrl: String = "",
    val imageCaption: String = "",
    val imageLocation: String = "",
    val visibleUserIds: List<String> = emptyList(), // size 0 if visible to all
    val isVisibleToAll: Boolean = visibleUserIds.isEmpty(), // If visibleUserIds is empty, then the image is visible to all users else only to the users in visibleUserIds list can see the image
    @ServerTimestamp val createdAt: Date = Date(),
)