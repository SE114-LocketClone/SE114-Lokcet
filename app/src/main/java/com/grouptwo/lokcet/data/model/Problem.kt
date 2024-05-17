package com.grouptwo.lokcet.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Problem(
    val problem: String = "",
    val email: String = "",
    val userId: String = "",
    val problemId: String = "",
    @ServerTimestamp val createdAt: Date = Date()
)

data class Suggestion(
    val suggestion: String = "",
    val email: String = "",
    val userId: String = "",
    val suggestionId: String = "",
    @ServerTimestamp val createdAt: Date = Date()
)