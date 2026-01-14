package com.google.ar.core.codelabs.hellogeospatial.models

import com.google.android.gms.maps.model.LatLng
import com.google.ar.core.codelabs.hellogeospatial.models.ModelPart

data class POILocation(
    val id: String,
    val position: LatLng,
    val name: String,
    val modelPath: String = "models/piramide_test.obj",
    val texturePath: String = "models/rocky_terrain_diff_4k.jpg",
    val baseScale: Float = 1.0f,
    // Base altitude offset in meters to raise/lower the model relative to the anchor height
    val baseAltitudeMeters: Float = 0f,
    val parts: List<ModelPart> = emptyList(),
    val description: String = "",
    val historicalInfo: String = "",
    val constructionPeriod: String = "",
    val height: String = "",
    val archaeologicalFeatures: List<String> = emptyList(),
    val culturalSignificance: String = "",
    val images: List<String> = emptyList(), // Resource names (without extension) for images in drawable-xxhdpi
    // Base yaw rotation (degrees) to align the model to site topography. Positive rotates to the right.
    val baseYawDeg: Float = 0f,
    val bobbingEnabled: Boolean = false,
    val bobbingAmplitude: Float = 0.2f,
    val bobbingSpeed: Float = 1f
)