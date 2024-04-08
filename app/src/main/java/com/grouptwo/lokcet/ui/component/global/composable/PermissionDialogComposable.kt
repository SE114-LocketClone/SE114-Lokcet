package com.grouptwo.lokcet.ui.component.global.composable

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.grouptwo.lokcet.ui.theme.YellowPrimary
import com.grouptwo.lokcet.R.string as strimgResource

@Composable
fun PermissionDialog(onRequestPermission: () -> Unit) {
    var showWarningDialog by remember { mutableStateOf(true) }
    if (showWarningDialog) {
        AlertDialog(

            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            onDismissRequest = { /*TODO*/ },
            confirmButton = {
                TextButton(
                    onClick = {
                        onRequestPermission()
                        showWarningDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        YellowPrimary
                    ), modifier = Modifier.padding(8.dp)
                ) {
                    Text(text = stringResource(id = strimgResource.request_notification_permission))
                }
            },
            title = { Text(text = stringResource(id = strimgResource.notification_permission_title)) },
            text = { Text(text = stringResource(id = strimgResource.notification_permission_description)) })
    }
}

@Composable
fun RationaleDialog() {
    var showWarningDialog by remember { mutableStateOf(true) }
    if (showWarningDialog) {
        AlertDialog(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            onDismissRequest = { showWarningDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showWarningDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        YellowPrimary
                    ), modifier = Modifier.padding(8.dp)
                ) {
                    Text(text = stringResource(id = strimgResource.ok))
                }
            },
            title = { Text(stringResource(id = strimgResource.notification_permission_title)) },
            text = { Text(stringResource(id = strimgResource.notification_permission_settings)) })
    }
}