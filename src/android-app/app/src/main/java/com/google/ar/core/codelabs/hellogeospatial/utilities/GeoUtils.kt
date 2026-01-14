package com.google.ar.core.codelabs.hellogeospatial.utilities

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

fun bearingBetweenDeg(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val φ1 = Math.toRadians(lat1)
    val φ2 = Math.toRadians(lat2)
    val Δλ = Math.toRadians(lon2 - lon1)
    val y = sin(Δλ) * cos(φ2)
    val x = cos(φ1) * sin(φ2) - sin(φ1) * cos(φ2) * cos(Δλ)
    var θ = Math.toDegrees(atan2(y, x))
    if (θ < 0) θ += 360.0
    return θ
}

fun normalizeDeg180(deg: Double): Double {
    var d = deg % 360.0
    if (d < -180.0) d += 360.0
    if (d > 180.0) d -= 360.0
    return d
}
