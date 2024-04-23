package com.grouptwo.lokcet.di.service

import com.google.firebase.firestore.GeoPoint

interface LocationService {
    fun checkLocationPermission(): Boolean
    suspend fun getCurrentLocation(): GeoPoint
    fun calculateDistanceBetweenTwoPoints(
        pointA: GeoPoint, pointB: GeoPoint
    ): Double
}