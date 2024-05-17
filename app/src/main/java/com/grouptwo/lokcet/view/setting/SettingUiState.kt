package com.grouptwo.lokcet.view.setting

import android.graphics.Bitmap
import android.net.Uri
import com.grouptwo.lokcet.data.model.User

data class SettingUiState(
    val currentUser: User? = null,
    val firstName : String = "",
    val lastName : String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val avatarUrl: String = "",
    val isNetworkAvailable: Boolean = false,
    val isLoadingUserData: Boolean = false,

    // change name
    val isChangeNameLoading: Boolean = false,
    val firstNameField: String = "",
    val lastNameField: String = "",
    val isButtonChangeNameEnabled: Boolean = false,

    // change phone
    val isChangePhoneLoading: Boolean = false,
    val phoneNumberField: String = "",
    val isButtonChangePhoneEnabled: Boolean = false,

    // Report a problem
    val reportProblem: String = "",
    val isReportProblemLoading: Boolean = false,
    val isButtonReportProblemEnabled: Boolean = false,

    // Make a suggestion
    val makeSuggestion: String = "",
    val isMakeSuggestionLoading: Boolean = false,
    val isButtonMakeSuggestionEnabled: Boolean = false,

    // Logout
    val isShowLogoutDialog: Boolean = false,
    // Delete account
    val isShowDeleteAccountDialog: Boolean = false,

    // Image upload
    val imagePickedUrl : Uri? = null,
    val isImageUpload: Boolean = false,
    val pickedImage: Bitmap? = null,

    val isShowAvatarBottomDialog: Boolean = false,
)