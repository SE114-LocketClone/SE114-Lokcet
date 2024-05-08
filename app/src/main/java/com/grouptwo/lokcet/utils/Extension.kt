package com.grouptwo.lokcet.utils

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Patterns
import android.view.View
import android.view.ViewTreeObserver
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import com.google.firebase.Timestamp
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

// Define validate function for app
fun String.isValidEmail(): Boolean {
    // Check if the email is not blank and matches the email pattern
    return this.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

private const val MIN_PASSWORD_LENGTH = 6

// Password must contain at least one uppercase letter,
// one lowercase letter, one number, and no whitespace.
private val PASSWORD_PATTERN =
    Regex("^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[!@#\$%^&*()_+=|<>?{}\\\\[\\\\]~-])(?=\\S+$)")

fun String.isValidPassword(): Boolean {
    // Check if the password is not blank and matches the password pattern
    return this.isNotBlank() && this.length >= MIN_PASSWORD_LENGTH
}

fun String.isValidName(): Boolean {
    // Check if the name is not blank
    return this.isNotBlank()
}

private val MAXIMUM_PHONE_NUMBER_LENGTH = 9

fun String.isValidPhoneNumber(): Boolean {
    // Check if the phone number is not blank and matches the phone number pattern
    return this.isNotBlank() && this.length == MAXIMUM_PHONE_NUMBER_LENGTH
}

fun String.isMatchingPassword(password: String): Boolean {
    // Check if the password matches the confirm password
    return this == password
}

@SuppressLint("ModifierFactoryUnreferencedReceiver")
@Composable
fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier {
    return composed {
        clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        )
    }
}

fun Bitmap.compressToJpeg(): ByteArray {
    val stream = ByteArrayOutputStream()
    // Compress the bitmap to JPEG format at 80% quality
    this.compress(Bitmap.CompressFormat.JPEG, 80, stream)
    return stream.toByteArray()
}

fun Bitmap.rotateBitmap(rotationDegrees: Int): Bitmap {
    val matrix = Matrix().apply {
        postRotate(-rotationDegrees.toFloat())
        postScale(-1f, -1f)
    }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

// Observer for listener to focus change in camera view
inline fun View.afterMeasured(crossinline block: () -> Unit) {
    if (measuredWidth > 0 && measuredHeight > 0) {
        block()
    } else {
        viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (measuredWidth > 0 && measuredHeight > 0) {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                    block()
                }
            }
        })
    }
}


fun Date.calculateTimePassed(currentServerTime: Timestamp): String {
    val milliseconds = currentServerTime.toDate().time - this.time
    val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)
    val minutes = TimeUnit.SECONDS.toMinutes(seconds)
    val hours = TimeUnit.SECONDS.toHours(seconds)
    val days = TimeUnit.SECONDS.toDays(seconds)
    val weeks = days / 7
    val months = days / 30
    val years = days / 365

    return when {
        years > 0 -> "$years năm trước"
        months > 0 -> "$months tháng trước"
        weeks > 0 -> "$weeks tuần trước"
        days > 0 -> "$days ngày trước"
        hours > 0 -> "$hours giờ trước"
        minutes > 0 -> "$minutes phút trước"
        seconds > 0 -> "$seconds giây trước"
        else -> "Vừa xong"
    }
}

fun returnTimeMinutes(currentServerTime: Date): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(currentServerTime)
}

//Extension function to get the friend id from current chat room id
fun String.getFriendId(currentUserId: String): String {
    return this.replace(currentUserId, "").replace("_", "")
}

fun Date.toDayMonth(
    currentServerTime: Timestamp
): String {
    val currentTimestamp = currentServerTime.toDate().time
    val timestamp = this.time

    val diff = currentTimestamp - timestamp
    val diffInMins = TimeUnit.MILLISECONDS.toMinutes(diff)
    val diffInHours = TimeUnit.MILLISECONDS.toHours(diff)

    return if (diffInMins < 1
    ) {
        "Vừa xong"
    } else if (diffInMins < 60) {
        "$diffInMins phút"
    } else if (diffInMins > 60 && diffInHours < 24) {
        "$diffInHours giờ"
    } else {
        val sdf = SimpleDateFormat("dd 'tháng' MM", Locale.getDefault())
        sdf.format(this)
    }
}


fun Date.toCustomTimeFormat(): String {
    val format = SimpleDateFormat("HH:mm dd 'tháng' MM 'năm' yyyy", Locale.getDefault())
    return format.format(this)
}

fun Date.toCustomDateFormat(): String {
    val format = SimpleDateFormat("dd 'tháng' MM 'năm' yyyy", Locale.getDefault())
    return format.format(this)
}