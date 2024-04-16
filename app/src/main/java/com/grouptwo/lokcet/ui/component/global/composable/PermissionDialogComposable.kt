package com.grouptwo.lokcet.ui.component.global.composable

import androidx.annotation.StringRes
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
import com.grouptwo.lokcet.ui.theme.BlackSecondary
import com.grouptwo.lokcet.ui.theme.YellowPrimary

@Composable
fun PermissionDialog(
    onRequestPermission: () -> Unit,
    @StringRes request: Int,
    @StringRes title: Int,
    @StringRes description: Int
) {
    var showWarningDialog by remember { mutableStateOf(true) }
    if (showWarningDialog) {
        AlertDialog(modifier = Modifier
            .wrapContentWidth()
            .wrapContentHeight(),
            onDismissRequest = { /*TODO*/ },
            confirmButton = {
                TextButton(
                    onClick = {
                        onRequestPermission()
                        showWarningDialog = false
                    }, colors = ButtonDefaults.buttonColors(
                        YellowPrimary
                    ), modifier = Modifier.padding(8.dp)
                ) {
                    Text(text = stringResource(id = request), color = BlackSecondary)
                }
            },
            title = { Text(text = stringResource(id = title)) },
            text = { Text(text = stringResource(id = description)) })
    }
}

//@Composable
//fun RationaleDialog(
//    @StringRes ok: Int,
//    @StringRes title: Int,
//    @StringRes description: Int
//)
// {
//    var showWarningDialog by remember { mutableStateOf(true) }
//    if (showWarningDialog) {
//        AlertDialog(
//            modifier = Modifier
//                .wrapContentWidth()
//                .wrapContentHeight(),
//            onDismissRequest = { showWarningDialog = false },
//            confirmButton = {
//                TextButton(
//                    onClick = {
//                        showWarningDialog = false
//                    },
//                    colors = ButtonDefaults.buttonColors(
//                        YellowPrimary
//                    ), modifier = Modifier.padding(8.dp)
//                ) {
//                    Text(text = stringResource(id = strimgResource.ok))
//                }
//            },
//            title = { Text(stringResource(id = strimgResource.notification_permission_title)) },
//            text = { Text(stringResource(id = strimgResource.notification_permission_settings)) })
//    }
//}

@Composable
fun RationaleDialog(
    @StringRes ok: Int, @StringRes title: Int, @StringRes description: Int
) {
    var showWarningDialog by remember { mutableStateOf(true) }
    if (showWarningDialog) {
        AlertDialog(modifier = Modifier
            .wrapContentWidth()
            .wrapContentHeight(),
            onDismissRequest = { showWarningDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showWarningDialog = false
                    }, colors = ButtonDefaults.buttonColors(
                        YellowPrimary
                    ), modifier = Modifier.padding(8.dp)
                ) {
                    Text(text = stringResource(id = ok), color = BlackSecondary)
                }
            },
            title = { Text(stringResource(id = title)) },
            text = { Text(stringResource(id = description)) })
    }
}