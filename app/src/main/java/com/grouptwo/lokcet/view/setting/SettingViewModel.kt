package com.grouptwo.lokcet.view.setting

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.viewModelScope
import com.grouptwo.lokcet.R
import com.grouptwo.lokcet.di.service.AccountService
import com.grouptwo.lokcet.di.service.InternetService
import com.grouptwo.lokcet.navigation.Screen
import com.grouptwo.lokcet.ui.component.global.snackbar.SnackbarManager
import com.grouptwo.lokcet.ui.component.global.snackbar.SnackbarMessage.Companion.toSnackbarMessage
import com.grouptwo.lokcet.utils.ConnectionState
import com.grouptwo.lokcet.utils.DataState
import com.grouptwo.lokcet.utils.compressToJpeg
import com.grouptwo.lokcet.utils.isValidPhoneNumber
import com.grouptwo.lokcet.view.LokcetViewModel
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val accountService: AccountService,
    private val internetService: InternetService,
    private val sharedPreferences: SharedPreferences,
    private val contentResolver: ContentResolver
) : LokcetViewModel() {
    private val networkStatus: StateFlow<ConnectionState> = internetService.networkStatus.stateIn(
        scope = viewModelScope,
        initialValue = ConnectionState.Unknown,
        started = SharingStarted.WhileSubscribed(5000)
    )
    private val _uiState = MutableStateFlow(SettingUiState())
    val uiState: StateFlow<SettingUiState> = _uiState.asStateFlow()

    init {
        launchCatching {
            networkStatus.collect { connectionState ->
                _uiState.update {
                    it.copy(isNetworkAvailable = connectionState == ConnectionState.Available || connectionState == ConnectionState.Unknown)
                }
                // Get user data to render UI
                getUserData()
            }
        }
    }

    fun getUserData() {
        launchCatching {
            try {
                // Check the internet connection
                if (_uiState.value.isNetworkAvailable.not()) {
                    throw Exception("Không có kết nối mạng")
                }
                _uiState.update {
                    it.copy(isLoadingUserData = true)
                }
                val user = accountService.getCurrentUser()
                _uiState.update {
                    it.copy(
                        currentUser = user,
                        firstName = user.firstName,
                        lastName = user.lastName,
                        phoneNumber = user.phoneNumber,
                        email = user.email,
                        avatarUrl = user.profilePicture,
                        isLoadingUserData = false
                    )
                }
            } catch (e: CancellationException) {
            } catch (e: Exception) {
                SnackbarManager.showMessage(e.toSnackbarMessage())
            }
        }
    }

    fun onClickChangeName(navigate: (String) -> Unit) {
        // Update the user's name
        _uiState.update {
            it.copy(
                firstNameField = _uiState.value.firstName, lastNameField = _uiState.value.lastName
            )
        }
        navigate(Screen.SettingScreen_1.route)
    }

    fun onFirstNameChange(firstName: String) {
        _uiState.update {
            it.copy(
                firstNameField = firstName,
                isButtonChangeNameEnabled = _uiState.value.lastNameField.isNotBlank() && firstName.isNotBlank()
            )
        }
    }

    fun onLastNameChange(lastName: String) {
        _uiState.update {
            it.copy(
                lastNameField = lastName,
                isButtonChangeNameEnabled = _uiState.value.firstNameField.isNotBlank() && lastName.isNotBlank()
            )
        }
    }

    fun onNameChange() {
        launchCatching {
            try {
                if (_uiState.value.isButtonChangeNameEnabled.not()) {
                    return@launchCatching // Do nothing
                }
                if (_uiState.value.isNetworkAvailable.not()) {
                    throw Exception("Không có kết nối mạng")
                }
                _uiState.update {
                    it.copy(isChangeNameLoading = true)
                }
                accountService.updateProfile(
                    _uiState.value.firstNameField,
                    _uiState.value.lastNameField,
                    _uiState.value.phoneNumber
                ).collect {
                    when (it) {
                        is DataState.Success -> {
                            val user = it.data
                            _uiState.update {
                                it.copy(
                                    isChangeNameLoading = false,
                                    firstName = _uiState.value.firstNameField,
                                    lastName = _uiState.value.lastNameField,
                                    // TODO: Update user data
                                    currentUser = user
                                )
                            }
                            SnackbarManager.showMessage(R.string.change_name_success)
                        }

                        is DataState.Error -> {
                            throw Exception(DataState.Error(it.exception).toString())
                        }

                        else -> {
                        }
                    }
                }
            } catch (e: CancellationException) {

            } catch (e: Exception) {
                SnackbarManager.showMessage(e.toSnackbarMessage())
                _uiState.update {
                    it.copy(
                        isChangeNameLoading = false,
                        firstNameField = _uiState.value.firstName,
                        lastNameField = _uiState.value.lastName
                    )
                }
            }
        }
    }

    fun onPhoneNumberChange(phoneNumber: String) {
        _uiState.update {
            it.copy(
                phoneNumberField = phoneNumber,
                isButtonChangePhoneEnabled = phoneNumber.isValidPhoneNumber()
            )
        }
    }

    fun onClickChangePhone(navigate: (String) -> Unit) {
        // Update the user's phone number
        _uiState.update {
            it.copy(
                phoneNumberField = _uiState.value.phoneNumber
            )
        }
        navigate(Screen.SettingScreen_2.route)
    }

    fun onPhoneChange() {
        launchCatching {
            try {
                if (_uiState.value.isButtonChangePhoneEnabled.not()) {
                    return@launchCatching // Do nothing
                }
                if (_uiState.value.isNetworkAvailable.not()) {
                    throw Exception("Không có kết nối mạng")
                }
                if (uiState.value.phoneNumberField != uiState.value.phoneNumber && accountService.isPhoneUsed(
                        _uiState.value.phoneNumberField
                    )
                ) {
                    SnackbarManager.showMessage(R.string.phone_used)
                    return@launchCatching
                }
                _uiState.update {
                    it.copy(isChangePhoneLoading = true)
                }
                accountService.updateProfile(
                    _uiState.value.firstName,
                    _uiState.value.lastName,
                    _uiState.value.phoneNumberField
                ).collect {
                    when (it) {
                        is DataState.Success -> {
                            val user = it.data
                            _uiState.update {
                                it.copy(
                                    phoneNumber = _uiState.value.phoneNumberField,
                                    currentUser = user,
                                    isChangePhoneLoading = false
                                )
                            }
                            SnackbarManager.showMessage(R.string.change_phone_success)
                        }

                        is DataState.Error -> {
                            throw Exception(DataState.Error(it.exception).toString())
                        }

                        else -> {
                        }
                    }
                }
            } catch (e: CancellationException) {
            } catch (e: Exception) {
                SnackbarManager.showMessage(e.toSnackbarMessage())
                _uiState.update {
                    it.copy(
                        isChangePhoneLoading = false, phoneNumberField = _uiState.value.phoneNumber
                    )
                }
            }
        }
    }

    fun onClickReportProblem(navigate: (String) -> Unit) {
        // Report a problem
        _uiState.update {
            it.copy(
                reportProblem = ""
            )
        }
        navigate(Screen.SettingScreen_3.route)
    }

    fun onReportProblemChange(reportProblem: String) {
        _uiState.update {
            it.copy(
                reportProblem = reportProblem,
                isButtonReportProblemEnabled = reportProblem.isNotBlank()
            )
        }
    }

    fun onReportProblem(popUp: () -> Unit) {
        launchCatching {
            try {
                if (_uiState.value.isButtonReportProblemEnabled.not()) {
                    return@launchCatching // Do nothing
                }
                if (_uiState.value.isNetworkAvailable.not()) {
                    throw Exception("Không có kết nối mạng")
                }
                _uiState.update {
                    it.copy(isReportProblemLoading = true)
                }
                accountService.reportProblem(
                    _uiState.value.reportProblem, _uiState.value.email, accountService.currentUserId
                ).collect {
                    when (it) {
                        is DataState.Success -> {
                            _uiState.update {
                                it.copy(
                                    reportProblem = "", isReportProblemLoading = false
                                )
                            }
                            SnackbarManager.showMessage(R.string.report_problem_success)
                            // Navigate back to the previous screen
                            popUp()
                        }

                        is DataState.Error -> {
                            throw Exception(DataState.Error(it.exception).toString())
                        }

                        else -> {
                        }
                    }
                }
            } catch (e: CancellationException) {
            } catch (e: Exception) {
                SnackbarManager.showMessage(e.toSnackbarMessage())
                _uiState.update {
                    it.copy(
                        isReportProblemLoading = false, reportProblem = ""
                    )
                }
            }
        }
    }

    fun onClickMakeSuggestion(navigate: (String) -> Unit) {
        // Make a suggestion
        _uiState.update {
            it.copy(
                makeSuggestion = "",
            )
        }
        navigate(Screen.SettingScreen_4.route)
    }

    fun onMakeSuggestionChange(makeSuggestion: String) {
        _uiState.update {
            it.copy(
                makeSuggestion = makeSuggestion,
                isButtonMakeSuggestionEnabled = makeSuggestion.isNotBlank()
            )
        }
    }

    fun onMakeSuggestions(popUp: () -> Unit) {
        launchCatching {
            try {
                if (_uiState.value.isButtonMakeSuggestionEnabled.not()) {
                    return@launchCatching // Do nothing
                }
                if (_uiState.value.isNetworkAvailable.not()) {
                    throw Exception("Không có kết nối mạng")
                }
                _uiState.update {
                    it.copy(isMakeSuggestionLoading = true)
                }
                accountService.makeSuggestions(
                    _uiState.value.makeSuggestion,
                    _uiState.value.email,
                    accountService.currentUserId
                ).collect {
                    when (it) {
                        is DataState.Success -> {
                            _uiState.update {
                                it.copy(
                                    makeSuggestion = "", isMakeSuggestionLoading = false
                                )
                            }
                            SnackbarManager.showMessage(R.string.make_suggestion_success)
                            // Navigate back to the previous screen
                            popUp()
                        }

                        is DataState.Error -> {
                            throw Exception(DataState.Error(it.exception).toString())
                        }

                        else -> {
                        }
                    }
                }
            } catch (e: CancellationException) {
            } catch (e: Exception) {
                SnackbarManager.showMessage(e.toSnackbarMessage())
                _uiState.update {
                    it.copy(
                        isMakeSuggestionLoading = false, makeSuggestion = ""
                    )
                }
            }
        }
    }

    fun onShowLogoutDialog(
        showLogoutDialog: Boolean
    ) {
        _uiState.update {
            it.copy(
                isShowLogoutDialog = showLogoutDialog
            )
        }
    }

    fun onShowDeleteAccountDialog(
        showDeleteAccountDialog: Boolean
    ) {
        _uiState.update {
            it.copy(
                isShowDeleteAccountDialog = showDeleteAccountDialog
            )
        }
    }

    fun onClickDeleteAccount(navigate: (String) -> Unit) {
        launchCatching {
            try {
                if (_uiState.value.isNetworkAvailable.not()) {
                    throw Exception("Không có kết nối mạng")
                }
                onShowDeleteAccountDialog(false)
                accountService.deleteAccount(accountService.currentUserId).collect {
                    when (it) {
                        is DataState.Success -> {
                            // Delete the account successfully
                            navigate(Screen.WelcomeScreen.route)
                        }

                        is DataState.Error -> {
                            throw Exception(DataState.Error(it.exception).toString())
                        }

                        else -> {
                        }
                    }
                }
            } catch (
                e: CancellationException
            ) {
            } catch (e: Exception) {
                SnackbarManager.showMessage(e.toSnackbarMessage())
            }
        }
    }

    fun onClickLogout(navigate: (String) -> Unit) {
        launchCatching {
            try {
                if (_uiState.value.isNetworkAvailable.not()) {
                    throw Exception("Không có kết nối mạng")
                }
                onShowLogoutDialog(false)
                // Remove saved user password
                sharedPreferences.edit().remove("password").apply()
                accountService.signOut()
                navigate(Screen.WelcomeScreen.route)
            } catch (e: Exception) {
                SnackbarManager.showMessage(e.toSnackbarMessage())
            }
        }
    }

    fun onImagePicked(uri: Uri?) {
        launchCatching {
            // Update the image uri
            _uiState.update {
                val bitmap = uri?.let {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        val source = ImageDecoder.createSource(contentResolver, uri)
                        ImageDecoder.decodeBitmap(source)
                    } else {
                        @Suppress("DEPRECATION") MediaStore.Images.Media.getBitmap(
                            contentResolver,
                            uri
                        )
                    }
                }
                it.copy(imagePickedUrl = uri, pickedImage = bitmap)
            }
            if (_uiState.value.pickedImage != null) {
                // Start upload image
                onImageCropped()
            }
        }
    }

    fun startCropping(
        activity: Activity, sourceUri: Uri, cropResultLauncher: ActivityResultLauncher<Intent>
    ) {
        val destinationUri = Uri.fromFile(File(activity.cacheDir, "cropped"))
        val intent = UCrop.of(sourceUri, destinationUri).withAspectRatio(1f, 1f).getIntent(activity)
        cropResultLauncher.launch(intent)
    }

    fun onImageCropped() {
        // Upload the cropped image
        launchCatching {
            try {
                if (_uiState.value.isNetworkAvailable.not()) {
                    throw Exception("Không có kết nối mạng")
                }
                _uiState.update {
                    it.copy(isImageUpload = true, isShowAvatarBottomDialog = false)
                }
                val image = _uiState.value.pickedImage?.compressToJpeg()
                if (image == null) {
                    throw Exception("Không thể tải ảnh lên")
                }
                val userId = accountService.currentUserId
                accountService.uploadProfileImage(userId, image).collect {
                    when (it) {
                        is DataState.Success -> {
                            val data = it.data
                            _uiState.update {
                                it.copy(
                                    avatarUrl = data, isImageUpload = false
                                )
                            }
                            SnackbarManager.showMessage(R.string.upload_image_success)
                        }

                        is DataState.Error -> {
                            throw Exception(DataState.Error(it.exception).toString())
                        }

                        else -> {
                        }
                    }
                }
            } catch (e: CancellationException) {
            } catch (e: Exception) {
                SnackbarManager.showMessage(e.toSnackbarMessage())
                _uiState.update {
                    it.copy(isImageUpload = false)
                }
            }
        }
    }

    fun onRemoveImage() {
        launchCatching {
            try {
                if (_uiState.value.isNetworkAvailable.not()) {
                    throw Exception("Không có kết nối mạng")
                }
                _uiState.update {
                    it.copy(isImageUpload = true, isShowAvatarBottomDialog = false)
                }
                val userId = accountService.currentUserId
                accountService.deleteProfileImage(userId, _uiState.value.firstName, _uiState.value.lastName).collect {
                    when (it) {
                        is DataState.Success -> {
                            val data = it.data
                            _uiState.update {
                                it.copy(
                                    avatarUrl = data, isImageUpload = false
                                )
                            }
                            SnackbarManager.showMessage(R.string.delete_image_success)
                        }

                        is DataState.Error -> {
                            throw Exception(DataState.Error(it.exception).toString())
                        }

                        else -> {
                        }
                    }
                }
            } catch (e: CancellationException) {
            } catch (e: Exception) {
                SnackbarManager.showMessage(e.toSnackbarMessage())
                _uiState.update {
                    it.copy(isImageUpload = false)
                }
            }
        }
    }

    fun onClickAvatarBottomDialog(showAvatarBottomDialog: Boolean) {
        _uiState.update {
            it.copy(
                isShowAvatarBottomDialog = showAvatarBottomDialog
            )
        }
    }
    fun onBackClick(popUp: () -> Unit) {
        popUp()
    }


}