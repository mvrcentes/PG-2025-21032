package com.google.ar.core.codelabs.hellogeospatial.utilities

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Calculate distance in meters between two geo coordinates
 */
public fun calculateDistanceInMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val earthRadius = 6371000.0 // Earth radius in meters

    val lat1Rad = Math.toRadians(lat1)
    val lat2Rad = Math.toRadians(lat2)
    val deltaLat = Math.toRadians(lat2 - lat1)
    val deltaLon = Math.toRadians(lon2 - lon1)

    val a = sin(deltaLat/2) * sin(deltaLat/2) +
            cos(lat1Rad) * cos(lat2Rad) *
            sin(deltaLon/2) * sin(deltaLon/2)
    val c = 2 * atan2(sqrt(a), sqrt(1-a))

    return earthRadius * c
}

/**
 * Calculate bearing in degrees from one geo coordinate to another
 */
public fun calculateBearing(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
    val lat1Rad = Math.toRadians(lat1)
    val lat2Rad = Math.toRadians(lat2)
    val deltaLonRad = Math.toRadians(lon2 - lon1)
    
    val y = sin(deltaLonRad) * cos(lat2Rad)
    val x = cos(lat1Rad) * sin(lat2Rad) - sin(lat1Rad) * cos(lat2Rad) * cos(deltaLonRad)
    
    val bearingRad = atan2(y, x)
    return Math.toDegrees(bearingRad).toFloat()
}