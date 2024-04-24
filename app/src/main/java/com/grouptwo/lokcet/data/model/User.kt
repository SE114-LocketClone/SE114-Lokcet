package com.grouptwo.lokcet.data.model

import com.google.firebase.firestore.GeoPoint

data class User(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val location: GeoPoint = GeoPoint(0.0, 0.0),
    val email: String = "",
    val password: String = "",
    val phoneNumber: String = "",
    val profilePicture: String = "",
    val friends: List<String> = emptyList(),
    val friendRequests: List<String> = emptyList(),
    val friendWaitList: List<String> = emptyList(),
    val uploadImageList: List<String> = emptyList(),
    val isOnline: Boolean = false,
    val isBannded: Boolean = false,
    val isDeleted: Boolean = false,
    val createdAt: Any = Any(),
    val lastSeen: Any = Any()
)