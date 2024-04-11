package com.grouptwo.lokcet.di.impl

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.firebase.firestore.GeoPoint
import com.grouptwo.lokcet.di.service.LocationService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class LocationServiceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val fusedLocationProviderClient: FusedLocationProviderClient
) : LocationService {
    override fun checkLocationPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED)
    }

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): GeoPoint {
        val defaultLocation = GeoPoint(0.0, 0.0)

        // Check for location permission
        if (!checkLocationPermission()) {
            return defaultLocation
        }
        // Call fusedLocationProviderClient to get the current location in coroutine
        return suspendCancellableCoroutine { continuation ->
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    continuation.resume(GeoPoint(location.latitude, location.longitude))
                } else {
                    continuation.resume(defaultLocation)
                }
            }.addOnFailureListener {
                continuation.resume(defaultLocation)
            }
        }
    }
}