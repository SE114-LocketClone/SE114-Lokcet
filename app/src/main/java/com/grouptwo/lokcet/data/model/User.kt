package com.grouptwo.lokcet.data.model

import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class User(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val fullName: String = "",
    val location: GeoPoint = GeoPoint(0.0, 0.0),
    val email: String = "",
    val password: String = "",
    val phoneNumber: String = "",
    val profilePicture: String = "",
    val friends: List<String> = emptyList(),
    val friendRequests: List<String> = emptyList(),
    val friendWaitList: List<String> = emptyList(),
    val uploadImageList: List<String> = emptyList(),
    @PropertyName("online") var isOnline: Boolean = false,
    @PropertyName("bannded") var isBannded: Boolean = false,
    @PropertyName("deleted") var isDeleted: Boolean = false,
    @ServerTimestamp val createdAt: Date = Date(),
    @ServerTimestamp val lastSeen: Date = Date()
)

data class FCMToken(
    val token: String = "",
    @ServerTimestamp val timestamp: Date = Date()
)