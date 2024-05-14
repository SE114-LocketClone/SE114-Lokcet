package com.grouptwo.lokcet.view.setting

import com.grouptwo.lokcet.data.model.User

data class SettingUiState(
    val currentUser: User? = null,
    val firstName : String = "",
    val lastName : String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val reportProblem: String = "",
    val avatarUrl: String = "",
    val isNetworkAvailable: Boolean = false,
    val isLoadingUserData: Boolean = false,
)