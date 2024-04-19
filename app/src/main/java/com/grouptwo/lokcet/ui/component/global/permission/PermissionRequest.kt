package com.grouptwo.lokcet.ui.component.global.permission

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.grouptwo.lokcet.R
import com.grouptwo.lokcet.ui.component.global.composable.PermissionDialog
import com.grouptwo.lokcet.ui.component.global.composable.RationaleDialog

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestNotificationPermission() {
    // Request notification permission
    val permissionState =
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
    if (!permissionState.status.isGranted) {
        if (permissionState.status.shouldShowRationale) RationaleDialog(
            ok = R.string.ok,
            title = R.string.notification_permission_title,
            description = R.string.notification_permission_description
        )
        else PermissionDialog(
            onRequestPermission = { permissionState.launchPermissionRequest() },
            request = R.string.request_notification_permission,
            title = R.string.notification_permission_title,
            description = R.string.notification_permission_description
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestLocationPermission() {
    // Request location permission
    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    if (!permissionState.allPermissionsGranted) {
        if (permissionState.shouldShowRationale) RationaleDialog(
            ok = R.string.ok,
            title = R.string.location_permission_title,
            description = R.string.location_permission_description
        )
        else PermissionDialog(
            onRequestPermission = { permissionState.launchMultiplePermissionRequest() },
            request = R.string.request_location_permission,
            title = R.string.location_permission_title,
            description = R.string.location_permission_description
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestContactPermission() {
    // Request contact permission
    val permissionState = rememberPermissionState(permission = Manifest.permission.READ_CONTACTS)
    if (!permissionState.status.isGranted) {
        if (permissionState.status.shouldShowRationale) RationaleDialog(
            ok = R.string.ok,
            title = R.string.contacts_permission_title,
            description = R.string.contacts_permission_description
        )
        else PermissionDialog(
            onRequestPermission = { permissionState.launchPermissionRequest() },
            request = R.string.request_contacts_permission,
            title = R.string.contacts_permission_title,
            description = R.string.contacts_permission_description
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestCameraPermission() {
    // Request camera permission
    val permissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    if (!permissionState.status.isGranted) {
        if (permissionState.status.shouldShowRationale) RationaleDialog(
            ok = R.string.ok,
            title = R.string.camera_permission_title,
            description = R.string.camera_permission_description
        )
        else PermissionDialog(
            onRequestPermission = { permissionState.launchPermissionRequest() },
            request = R.string.request_camera_permission,
            title = R.string.camera_permission_title,
            description = R.string.camera_permission_description
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestStoragePermission() {
    // Request storage permission
    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    )
    if (!permissionState.allPermissionsGranted) {
        if (permissionState.shouldShowRationale) RationaleDialog(
            ok = R.string.ok,
            title = R.string.storage_permission_title,
            description = R.string.storage_permission_description
        )
        else PermissionDialog(
            onRequestPermission = { permissionState.launchMultiplePermissionRequest() },
            request = R.string.request_storage_permission,
            title = R.string.storage_permission_title,
            description = R.string.storage_permission_description
        )
    }
}