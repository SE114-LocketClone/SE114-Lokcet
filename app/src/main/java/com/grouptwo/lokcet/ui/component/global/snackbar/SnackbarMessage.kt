package com.grouptwo.lokcet.ui.component.global.snackbar

import android.content.res.Resources
import androidx.annotation.StringRes
import com.grouptwo.lokcet.R.string as stringResource

// Create a sealed class to manage the Snackbar Message state
sealed class SnackbarMessage {
    // Use seal class and 2 class to make sure only 2 valid type of SnackbarMessage are allowed
    // StringSnackbar class to display message from string
    // ResourceSnackbar class to display message from resources
    class StringSnackbar(val message: String) : SnackbarMessage()
    class ResourceSnackbar(@StringRes val message: Int) : SnackbarMessage()

    companion object{
        // Create a function to convert SnackbarMessage to message to be displayed in Snackbar
        // If SnackbarMessage is StringSnackbar, return the message
        // If SnackbarMessage is ResourceSnackbar, return the message from resources
        fun SnackbarMessage.toMessage(resources: Resources): String{
            return when(this){
                is StringSnackbar ->this.message
                is ResourceSnackbar -> resources.getString(this.message)
            }
        }

        // Create a function to convert Throwable exception to SnackbarMessage
        // If Throwable exception has message, return the message as StringSnackbar
        // If Throwable exception has no message, return generic error message as ResourceSnackbar
        fun Throwable.toSnackbarMessage(): SnackbarMessage{
          val message = this.message.orEmpty()
            return if(message.isNotBlank()) StringSnackbar(message)
            else ResourceSnackbar(stringResource.generic_error)
        }
    }
}