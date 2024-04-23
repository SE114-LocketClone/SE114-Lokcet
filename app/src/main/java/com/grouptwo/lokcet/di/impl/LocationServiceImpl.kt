package com.grouptwo.lokcet.di.impl

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.firestore.GeoPoint
import com.grouptwo.lokcet.di.service.LocationService
import com.grouptwo.lokcet.utils.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

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

    override fun calculateDistanceBetweenTwoPoints(pointA: GeoPoint, pointB: GeoPoint): Double {
        val dLat = Math.toRadians(pointB.latitude - pointA.latitude)
        val dLon = Math.toRadians(pointB.longitude - pointA.longitude)

        val lat1 = Math.toRadians(pointA.latitude)
        val lat2 = Math.toRadians(pointB.latitude)

        val a = sin(dLat / 2).pow(2.0) + sin(dLon / 2).pow(2.0) * cos(lat1) * cos(lat2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return Constants.EARTH_RADIUS * c
    }
}