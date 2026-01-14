package com.google.ar.core.codelabs.hellogeospatial.navigation

import com.google.android.gms.maps.model.LatLng
import com.google.ar.core.codelabs.hellogeospatial.utilities.calculateDistanceInMeters

/**
 * Gestiona un recorrido por waypoints (LatLng). Avanza automáticamente
 * al siguiente cuando el usuario entra dentro de 'arrivalThresholdMeters'.
 */
class RouteNavigator(
    waypoints: List<LatLng>,
    private val arrivalThresholdMeters: Double = 12.0  // ajusta el umbral a tu gusto
) {
    private val route: List<LatLng> = waypoints.toList()
    private var index: Int = 0

    private var lastDistance: Double = Double.NaN
    fun currentIndex(): Int = index
    fun lastDistanceMeters(): Double = lastDistance

    fun updateAndGetTarget(currentLat: Double, currentLon: Double): LatLng? {
        val target = currentTarget() ?: return null
        val d = calculateDistanceInMeters(currentLat, currentLon, target.latitude, target.longitude)
        lastDistance = d
        if (d <= arrivalThresholdMeters) index++
        return currentTarget()
    }

    /** Devuelve el target actual (o null si terminó el recorrido). */
    fun currentTarget(): LatLng? = if (index in route.indices) route[index] else null

    /** Reinicia al primer waypoint. */
    fun reset() { index = 0 }

    /** ¿Terminó el recorrido? */
    fun isFinished(): Boolean = index !in route.indices

    /**
     * Evalúa la distancia al target actual. Si estás dentro del umbral,
     * avanza al siguiente. Devuelve el target vigente tras evaluar.
     */


    /** Avanza manualmente (opcional). */
    fun advance() { if (index in route.indices) index++ }
}
