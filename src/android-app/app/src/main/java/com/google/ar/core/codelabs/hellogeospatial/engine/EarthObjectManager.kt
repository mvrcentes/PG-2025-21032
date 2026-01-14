package com.google.ar.core.codelabs.hellogeospatial.engine

import android.content.Context
import android.util.Log
import com.google.ar.core.Anchor
import com.google.ar.core.Earth
import com.google.ar.core.TrackingState
import com.google.ar.core.codelabs.hellogeospatial.models.POILocation
import com.google.ar.core.codelabs.hellogeospatial.config.HelloGeoConfig
import com.google.ar.core.codelabs.hellogeospatial.utilities.getPoiLocations
import com.google.ar.core.codelabs.hellogeospatial.models.ModelPart
import com.google.ar.core.examples.java.common.samplerender.Mesh
import com.google.ar.core.examples.java.common.samplerender.SampleRender
import com.google.ar.core.examples.java.common.samplerender.Shader
import com.google.ar.core.examples.java.common.samplerender.Texture

/**
 * Loads POI models/textures and manages Earth anchors for them.
 */
class EarthObjectManager(
    private val context: Context,
    private val render: SampleRender,
    private val defaultShader: Shader
) {
    companion object { private const val TAG = "EarthObjectManager" }

    private val poiList: List<POILocation> = getPoiLocations(context)

    private val objectMeshes = HashMap<String, Mesh>()
    private val objectTextures = HashMap<String, Texture>()
    private val anchors = HashMap<String, Anchor>()

    var hasPlaced = false
        private set

    fun pois(): List<POILocation> = poiList
    fun anchorFor(id: String): Anchor? = anchors[id]
    fun allAnchors(): Map<String, Anchor> = anchors

    fun ensureAssetsLoaded() {
        for (poi in poiList) {
            if (poi.parts.isEmpty()) {
                val modelPath = poi.modelPath
                val texturePath = poi.texturePath
                if (!objectMeshes.containsKey(modelPath)) {
                    objectMeshes[modelPath] = Mesh.createFromAsset(render, modelPath)
                }
                if (!objectTextures.containsKey(texturePath)) {
                    objectTextures[texturePath] = Texture.createFromAsset(
                        render,
                        texturePath,
                        Texture.WrapMode.CLAMP_TO_EDGE,
                        Texture.ColorFormat.SRGB
                    )
                }
            } else {
                poi.parts.forEach { part ->
                    if (!objectMeshes.containsKey(part.modelPath)) {
                        objectMeshes[part.modelPath] = Mesh.createFromAsset(render, part.modelPath)
                    }
                    if (!objectTextures.containsKey(part.texturePath)) {
                        objectTextures[part.texturePath] = Texture.createFromAsset(
                            render,
                            part.texturePath,
                            Texture.WrapMode.CLAMP_TO_EDGE,
                            Texture.ColorFormat.SRGB
                        )
                    }
                }
            }
        }
    }

    fun meshFor(poi: POILocation): Mesh = objectMeshes[poi.modelPath]
        ?: Mesh.createFromAsset(render, poi.modelPath)

    fun textureFor(poi: POILocation): Texture = objectTextures[poi.texturePath]
        ?: Texture.createFromAsset(
            render,
            poi.texturePath,
            Texture.WrapMode.CLAMP_TO_EDGE,
            Texture.ColorFormat.SRGB
        )

    fun meshesFor(poi: POILocation): List<Pair<Mesh, Texture>> =
        if (poi.parts.isEmpty()) {
            listOf(meshFor(poi) to textureFor(poi))
        } else {
            poi.parts.map { part ->
                val mesh = objectMeshes[part.modelPath]
                    ?: Mesh.createFromAsset(render, part.modelPath)
                val tex = objectTextures[part.texturePath]
                    ?: Texture.createFromAsset(
                        render,
                        part.texturePath,
                        Texture.WrapMode.CLAMP_TO_EDGE,
                        Texture.ColorFormat.SRGB
                    )
                mesh to tex
            }
        }

    fun placeAllIfNeeded(earth: Earth) {
        if (hasPlaced || earth.trackingState != TrackingState.TRACKING) return
        placeAll(earth)
        hasPlaced = true
    }

    fun placeAll(earth: Earth) {
        for (poi in poiList) place(earth, poi)
    }

    fun place(earth: Earth, poi: POILocation) {
        anchors[poi.id]?.detach()

        // Terrain-aware altitude approximation:
        // Place at ground level by using camera altitude minus eye height,
        // then add any per-POI baseAltitudeMeters for local topography alignment.
        val cameraAlt = earth.cameraGeospatialPose.altitude
        val baseAltOffset = poi.baseAltitudeMeters.toDouble()
        val targetAltitude = (cameraAlt - HelloGeoConfig.CAMERA_EYE_HEIGHT_METERS) + baseAltOffset

        val anchor = earth.createAnchor(
            poi.position.latitude,
            poi.position.longitude,
            targetAltitude,
            0f, 0f, 0f, 1f
        )
        anchors[poi.id] = anchor
        Log.d(TAG, "Placed anchor for ${poi.id} at ${poi.position} (alt=${"%.2f".format(targetAltitude)})")
    }

    fun clear() {
        anchors.values.forEach { it.detach() }
        anchors.clear()
        hasPlaced = false
    }
}
