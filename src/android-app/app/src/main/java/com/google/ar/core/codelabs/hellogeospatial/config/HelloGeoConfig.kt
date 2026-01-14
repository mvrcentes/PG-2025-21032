package com.google.ar.core.codelabs.hellogeospatial.config

object HelloGeoConfig {
    const val Z_NEAR = 0.1f
    const val Z_FAR = 1000f

    // Distance threshold in meters to render POIs
    const val VISIBILITY_RADIUS_METERS = 100.0

    // Tap detection guard
    const val TAP_DELAY_MS: Long = 300

    // Model transform limits
    const val MIN_SCALE = 0.2f
    const val MAX_SCALE = 5f

    // Guided tour thresholds
    const val GUIDED_ARRIVAL_METERS = 10.0 //10m
    // Smaller arrival radius used ONLY for intermediate connectors between POIs
    const val CONNECTOR_ARRIVAL_METERS = 2.0 //2m
    const val TOUR_START_RADIUS_METERS = 20.0 //20m

    // Guided tour start location (approx). Update as needed.
    const val TOUR_START_LAT = 14.63128471
    const val TOUR_START_LON = -90.54847717

    // Loading/Ready thresholds
    const val ACCURACY_GOOD_METERS = 5.0
    const val HEADING_GOOD_DEGREES = 10.0

    // Lighting parameters for 3D models
    // Ambient light: base brightness level (0.0 = completely dark, 1.0 = full brightness)
    const val LIGHT_AMBIENT = 0.9f
    // Diffuse light: directional light contribution (0.0 = no directional, 1.0 = full directional)
    const val LIGHT_DIFFUSE = 0.7f
    // Light direction in view space (x, y, z) - normalized internally by shader
    // Default: light coming from upper-right of camera view
    val LIGHT_DIRECTION_VS = floatArrayOf(0.3f, 0.7f, 0.6f)

    // Terrain anchor approximation: assume average camera eye height above ground (in meters)
    // This is used to place anchors near terrain level even on uneven ground.
    const val CAMERA_EYE_HEIGHT_METERS = 1.6
}
