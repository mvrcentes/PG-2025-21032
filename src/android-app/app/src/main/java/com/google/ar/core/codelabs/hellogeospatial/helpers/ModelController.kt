package com.google.ar.core.codelabs.hellogeospatial.helpers

import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.RequiresApi
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.google.ar.core.HitResult
import kotlin.math.sin
import com.google.ar.core.codelabs.hellogeospatial.helpers.HapticFeedback

class ModelController {

    @RequiresApi(Build.VERSION_CODES.O)
    fun touchModel(context: Context, node: TransformableNode) {
        node.setOnTouchListener { _, _ ->
            HapticFeedback.touchModel(context)
            true
        }
    }

}