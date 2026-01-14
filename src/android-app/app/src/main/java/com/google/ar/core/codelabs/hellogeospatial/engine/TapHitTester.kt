package com.google.ar.core.codelabs.hellogeospatial.engine

import android.opengl.Matrix
import android.util.DisplayMetrics
import com.google.ar.core.Anchor
import com.google.ar.core.codelabs.hellogeospatial.models.POILocation

/**
 * Performs a simple screen-space AABB hit test for a POI 3D model.
 * Caller must provide current view and projection matrices.
 */
object TapHitTester {
    private val modelMatrix = FloatArray(16)
    private val modelViewMatrix = FloatArray(16)
    private val modelViewProjectionMatrix = FloatArray(16)

    // Default bounds (local space) – tune per model as needed
    private val defaultBounds = floatArrayOf(
        -2.0f, 0.0f, -2.0f,
         2.0f, 3.0f,  2.0f
    )

    fun isTapInside(
        screenX: Float,
        screenY: Float,
        anchor: Anchor,
        poi: POILocation,
        viewMatrix: FloatArray,
        projectionMatrix: FloatArray,
        metrics: DisplayMetrics,
        bounds: FloatArray? = null
    ): Boolean {
        anchor.pose.toMatrix(modelMatrix, 0)

        Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, modelViewMatrix, 0)

        val b = bounds ?: defaultBounds

        val corners = arrayOf(
            floatArrayOf(b[0], b[1], b[2], 1f),
            floatArrayOf(b[3], b[1], b[2], 1f),
            floatArrayOf(b[3], b[1], b[5], 1f),
            floatArrayOf(b[0], b[1], b[5], 1f),
            floatArrayOf(b[0], b[4], b[2], 1f),
            floatArrayOf(b[3], b[4], b[2], 1f),
            floatArrayOf(b[3], b[4], b[5], 1f),
            floatArrayOf(b[0], b[4], b[5], 1f)
        )

        var minX = Float.MAX_VALUE
        var maxX = Float.MIN_VALUE
        var minY = Float.MAX_VALUE
        var maxY = Float.MIN_VALUE
        var valid = 0

        val screenW = metrics.widthPixels.toFloat()
        val screenH = metrics.heightPixels.toFloat()

        val out = FloatArray(4)
        for (c in corners) {
            Matrix.multiplyMV(out, 0, modelViewProjectionMatrix, 0, c, 0)
            if (out[3] != 0f && out[3] > 0f) {
                val ndcX = out[0] / out[3]
                val ndcY = out[1] / out[3]
                val ndcZ = out[2] / out[3]
                if (ndcZ < -1f || ndcZ > 1f) continue
                val sx = (ndcX + 1f) * 0.5f * screenW
                val sy = (1f - ndcY) * 0.5f * screenH
                minX = kotlin.math.min(minX, sx)
                maxX = kotlin.math.max(maxX, sx)
                minY = kotlin.math.min(minY, sy)
                maxY = kotlin.math.max(maxY, sy)
                valid++
            }
        }
        if (valid == 0) return false
        val marginX = (maxX - minX) * 0.1f
        val marginY = (maxY - minY) * 0.1f
        minX -= marginX; maxX += marginX
        minY -= marginY; maxY += marginY
        return screenX in minX..maxX && screenY in minY..maxY
    }
}
