package com.google.ar.core.codelabs.hellogeospatial.helpers

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.RequiresApi

object HapticFeedback {

    @RequiresApi(Build.VERSION_CODES.O)
    private fun vibrate(context: Context, effect: VibrationEffect) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(effect)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun touchModel(context: Context) {
        val effect = VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE)
        vibrate(context, effect)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun rotateModel(context: Context) {
        val effect = VibrationEffect.createWaveform(longArrayOf(0, 20, 30, 20), -1)
        vibrate(context, effect)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun zoomModel(context: Context) {
        val effect = VibrationEffect.createWaveform(longArrayOf(0, 10, 10, 30), -1)
        vibrate(context, effect)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun confirmAction(context: Context) {
        val effect = VibrationEffect.createOneShot(60, VibrationEffect.EFFECT_TICK)
        vibrate(context, effect)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun showInfoPanel(context: Context) {
        val effect = VibrationEffect.createOneShot(30, VibrationEffect.EFFECT_DOUBLE_CLICK)
        vibrate(context, effect)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun navigationTap(context: Context) {
        val effect = VibrationEffect.createOneShot(80, VibrationEffect.EFFECT_TICK)
        vibrate(context, effect)
    }

}