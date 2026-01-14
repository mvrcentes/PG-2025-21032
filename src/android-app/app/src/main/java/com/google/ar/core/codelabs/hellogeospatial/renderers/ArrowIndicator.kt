package com.google.ar.core.codelabs.hellogeospatial.ui

import android.opengl.Matrix
import com.google.ar.core.examples.java.common.samplerender.Framebuffer
import com.google.ar.core.examples.java.common.samplerender.Mesh
import com.google.ar.core.examples.java.common.samplerender.SampleRender
import com.google.ar.core.examples.java.common.samplerender.Shader
import com.google.ar.core.examples.java.common.samplerender.Texture
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * Dibuja una flecha (flecha.obj) como HUD 3D siempre visible frente a la cámara.
 * Gira en yaw para apuntar hacia el siguiente waypoint.
 */
class ArrowIndicator(
    render: SampleRender,
    defaultShader: Shader,
    private val framebuffer: Framebuffer,
    modelPath: String = "models/flecha.obj",
    texturePath: String? = null,
    /** Distancia de la flecha frente a la cámara (m). */
    private val hudDistanceMeters: Float = 1.2f,
    /** Offset vertical en metros (negativo = un poquito abajo del centro). */
    private val hudYOffsetMeters: Float = -0.10f,
    /**
     * Corrección de yaw del modelo para que "0°" apunte hacia delante.
     * Ajusta si tu flecha por defecto no mira al -Z.
     */
    private val modelYawOffsetDeg: Float = 0f,
    /** Rotación local del modelo alrededor de X (pitch) para corregir vertical */
    private val modelPitchOffsetDeg: Float = 0f,
    /** Rotación local del modelo alrededor de Z (roll) para corregir ladeo */
    private val modelRollOffsetDeg: Float = 0f
) {
    private val modelMatrix = FloatArray(16)
    private val localMatrix = FloatArray(16)

    private val yawM = FloatArray(16)
    private val offsetM = FloatArray(16)
    private val transM = FloatArray(16)
    private val tmpM1 = FloatArray(16)
    private val tmpM2 = FloatArray(16)


    private val cameraWorldMatrix = FloatArray(16)
    private val modelViewMatrix = FloatArray(16)
    private val modelViewProjectionMatrix = FloatArray(16)
    private val invView = FloatArray(16)

    private val mesh: Mesh = Mesh.createFromAsset(render, modelPath)
    private val shader: Shader
    private val texture: Texture?

    // Yaw actual acumulado (grados) para evitar saltos en ±180
    private var currentYawDeg: Float = 0f
    // Límite de giro por frame (suavizado). Sube/baja según prefieras.
    private val maxYawStepDeg: Float = 12f

    private var initializedYaw = false

    // Parámetros de estabilidad
    private val deadzoneDeg   = 2f   // no muevas si el error < 2° (quita jitter)


    init {
        texture = texturePath?.let {
            Texture.createFromAsset(render, it, Texture.WrapMode.CLAMP_TO_EDGE, Texture.ColorFormat.SRGB)
        }
        shader = if (texture != null) {
            defaultShader.setTexture("u_Texture", texture)
        } else {
            defaultShader
        }
    }

    /**
     * Dibuja la flecha apuntando a (targetLat, targetLon).
     *
     * @param viewMatrix         Matriz view (cámara) del frame actual.
     * @param projectionMatrix   Matriz projection del frame actual.
     * @param cameraLat          Latitud de la cámara (deg).
     * @param cameraLon          Longitud de la cámara (deg).
     * @param cameraHeadingDeg   Heading de la cámara (deg, 0=Norte, 90=Este).
     * @param targetLat          Latitud del target.
     * @param targetLon          Longitud del target.
     */
    fun draw(
        render: SampleRender,
        viewMatrix: FloatArray,
        projectionMatrix: FloatArray,
        cameraLat: Double,
        cameraLon: Double,
        cameraHeadingDeg: Double,
        targetLat: Double,
        targetLon: Double
    ) {
        // 1) Bearing (0..360) hacia el target
    val bearingToTarget = bearingBetweenDeg(cameraLat, cameraLon, targetLat, targetLon)

// 2) Heading normalizado (0..360). Algunos dispositivos lo dan negativo.
    // Importante: en coordenadas de OpenGL/Android, un yaw positivo rota antihorario;
    // para que la flecha gire hacia la derecha cuando el objetivo está a la derecha,
    // usamos heading - bearing (no bearing - heading).
    val deltaYawShort = normalizeDeg180((cameraHeadingDeg - bearingToTarget).toFloat())

// 3) Aplica tus offsets del modelo (solo orientación local del mesh)
        Matrix.setIdentityM(offsetM, 0)
        Matrix.setRotateM(tmpM1, 0, modelYawOffsetDeg,   0f, 1f, 0f) // yaw local
        Matrix.setRotateM(tmpM2, 0, modelPitchOffsetDeg, 1f, 0f, 0f) // pitch local
        Matrix.multiplyMM(offsetM, 0, tmpM1, 0, tmpM2, 0)
        Matrix.setRotateM(tmpM1, 0, modelRollOffsetDeg,  0f, 0f, 1f) // roll local
        Matrix.multiplyMM(offsetM, 0, offsetM, 0, tmpM1, 0)

// 4) OBJETIVO DE YAW (mundo) = delta hacia target
        val targetYawDeg = if (!initializedYaw) {
            initializedYaw = true
            deltaYawShort
        } else {
            // Si prefieres target “continuo” cerca del current:
            unwrapToNear(currentYawDeg, deltaYawShort)
        }
// 5) Suaviza: limita grados por frame para giro estable y continuo
        val diffShort = shortestDeltaDeg(currentYawDeg, targetYawDeg)
        val step = if (kotlin.math.abs(diffShort) < deadzoneDeg) {
            0f
        } else {
            diffShort.coerceIn(-maxYawStepDeg, maxYawStepDeg)
        }
        currentYawDeg = normalizeDeg180(currentYawDeg + step)

// 6) Construye rotación hacia el target con yaw CONTINUO
        Matrix.setRotateM(yawM, 0, currentYawDeg, 0f, 1f, 0f)

// 7) Translación para HUD, y composición final: local = T * (Yaw * Offsets)
        Matrix.setIdentityM(transM, 0)
        Matrix.translateM(transM, 0, 0f, hudYOffsetMeters, -hudDistanceMeters)
        Matrix.multiplyMM(tmpM1, 0, yawM, 0, offsetM, 0)
        Matrix.multiplyMM(localMatrix, 0, transM, 0, tmpM1, 0)

// 8) model = inv(view) * local, MVP y draw (igual que ya tenías)
        Matrix.invertM(invView, 0, viewMatrix, 0)
        Matrix.multiplyMM(modelMatrix, 0, invView, 0, localMatrix, 0)
        Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, modelViewMatrix, 0)
    // Reasigna textura por seguridad (siempre la de la flecha)
    texture?.let { shader.setTexture("u_Texture", it) }
    shader.setMat4("u_ModelViewProjection", modelViewProjectionMatrix)
        render.draw(mesh, shader, framebuffer)


    }

    private fun normalize360(deg: Double): Double {
        var d = deg % 360.0
        if (d < 0) d += 360.0
        return d
    }

    private fun normalizeDeg180(deg: Float): Float {
        var d = (deg % 360f)
        if (d > 180f) d -= 360f
        if (d < -180f) d += 360f
        return d
    }

    /** Diferencia angular más corta (to - from) en [-180, 180] */
    private fun shortestDeltaDeg(fromDeg: Float, toDeg: Float): Float {
        return normalizeDeg180(toDeg - fromDeg)
    }

    /** Interpola en ángulo siguiendo el camino corto */
    private fun lerpAngleDeg(fromDeg: Float, toDeg: Float, alpha: Float): Float {
        val delta = shortestDeltaDeg(fromDeg, toDeg)
        return normalizeDeg180(fromDeg + delta * alpha)
    }

    /** Devuelve target “desenrollado” cercano a current (evita saltos de 360°). */
    private fun unwrapToNear(current: Float, target: Float): Float {
        var t = target
        var diff = t - current
        while (diff > 180f) {
            t -= 360f
            diff = t - current
        }
        while (diff < -180f) {
            t += 360f
            diff = t - current
        }
        return t
    }


    /** Bearing inicial de punto A a B en grados (0=Norte, sentido horario). */
    private fun bearingBetweenDeg(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        // fórmulas con radianes
        val φ1 = Math.toRadians(lat1)
        val φ2 = Math.toRadians(lat2)
        val Δλ = Math.toRadians(lon2 - lon1)
        val y = sin(Δλ) * cos(φ2)
        val x = cos(φ1) * sin(φ2) - sin(φ1) * cos(φ2) * cos(Δλ)
        var θ = Math.toDegrees(atan2(y, x))
        if (θ < 0) θ += 360.0
        return θ
    }

    private fun normalizeDeg(deg: Double): Double {
        var d = deg % 360.0
        if (d < -180.0) d += 360.0
        if (d > 180.0) d -= 360.0
        return d
    }
}
