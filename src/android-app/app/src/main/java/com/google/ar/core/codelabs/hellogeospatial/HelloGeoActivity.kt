/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.ar.core.codelabs.hellogeospatial

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.core.TrackingState
import com.google.ar.core.codelabs.hellogeospatial.helpers.ARCoreSessionLifecycleHelper
import com.google.ar.core.codelabs.hellogeospatial.helpers.GeoPermissionsHelper
import com.google.ar.core.codelabs.hellogeospatial.helpers.HelloGeoView
import com.google.ar.core.codelabs.hellogeospatial.helpers.HapticFeedback
import com.google.ar.core.examples.java.common.helpers.FullScreenHelper
import com.google.ar.core.examples.java.common.samplerender.SampleRender
import com.google.ar.core.exceptions.CameraNotAvailableException
import com.google.ar.core.exceptions.UnavailableApkTooOldException
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException
import com.google.ar.core.exceptions.UnavailableSdkTooOldException
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException
import com.google.ar.core.exceptions.UnsupportedConfigurationException

// Import the POILocation class
import com.google.ar.core.codelabs.hellogeospatial.models.POILocation
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector



class   HelloGeoActivity : AppCompatActivity() {
  companion object {
    private const val TAG = "HelloGeoActivity"
  }

  private lateinit var gestureDetector: GestureDetector
  /** Detector de pinch-to-zoom */
  private lateinit var scaleGestureDetector: ScaleGestureDetector


  lateinit var arCoreSessionHelper: ARCoreSessionLifecycleHelper
  lateinit var view: HelloGeoView
  lateinit var renderer: HelloGeoRenderer

  // Instructions overlay
  private lateinit var instructionsOverlay: View

  @SuppressLint("ClickableViewAccessibility")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Setup ARCore session lifecycle helper and configuration.
    arCoreSessionHelper = ARCoreSessionLifecycleHelper(this)
    // If Session creation or Session.resume() fails, display a message and log detailed
    // information.
    arCoreSessionHelper.exceptionCallback =
      { exception ->
        val message =
          when (exception) {
            is UnavailableUserDeclinedInstallationException ->
              "Please install Google Play Services for AR"
            is UnavailableApkTooOldException -> "Please update ARCore"
            is UnavailableSdkTooOldException -> "Please update this app"
            is UnavailableDeviceNotCompatibleException -> "This device does not support AR"
            is CameraNotAvailableException -> "Camera not available. Try restarting the app."
            is UnsupportedConfigurationException -> "This device doesn't support Geospatial features"
            else -> "Failed to create AR session: $exception"
          }
        Log.e(TAG, "ARCore threw an exception", exception)
        view.snackbarHelper.showError(this, message)
      }

    // Configure session features.
    arCoreSessionHelper.beforeSessionResume = ::configureSession
    lifecycle.addObserver(arCoreSessionHelper)

    // Set up the Hello AR renderer.
    renderer = HelloGeoRenderer(this)
    lifecycle.addObserver(renderer)

    // Set up Hello AR UI.
    view = HelloGeoView(this)
    lifecycle.addObserver(view)
    setContentView(view.root)

    // Sets up an example renderer using our HelloGeoRenderer.
    SampleRender(view.surfaceView, renderer, assets)

    // =============== SETUP BOTONES AR ESTÉTICOS ===============
    setupArButtons()

    // Initialize instructions overlay
    instructionsOverlay = findViewById(R.id.instructions_overlay)
    setupInstructionsOverlay()

    gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
      override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
      ): Boolean {
        // Ajusta este factor a tu gusto: porcentaje de grados por pixel movido
        val ROTATION_FACTOR = 0.2f
        // Scroll hacia la derecha (distanceX < 0) → rotación positiva
        renderer.updateRotation(-distanceX * ROTATION_FACTOR)
        return true
      }
    })

    scaleGestureDetector = ScaleGestureDetector(this,
      object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
          // detector.scaleFactor > 1 → zoom in; <1 → zoom out
          renderer.updateScale(detector.scaleFactor)
          return true
        }
      })

    view.surfaceView.setOnTouchListener { _, event ->
      // 1) Gestos de pinch-to-zoom
      scaleGestureDetector.onTouchEvent(event)
      // 2) Gestos de scroll horizontal (rotación)
      gestureDetector.onTouchEvent(event)
      // 3) Tap para hit-test en ACTION_UP
      if (event.action == MotionEvent.ACTION_UP) {
        renderer.onScreenTap(event.x, event.y)
      }
      true
    }


  }

  // =============== CONFIGURACIÓN DE BOTONES AR ESTÉTICOS ===============
  private fun setupArButtons() {
    // Botón de información: usado también como "Mostrar más información" en modo guiado
    findViewById<ImageButton>(R.id.btn_info)?.setOnClickListener {
      renderer.showCurrentPoiInfoIfClose()
      onInfoButtonClicked()
    }

    // Botón de regresar
    findViewById<ImageButton>(R.id.btn_back)?.setOnClickListener {
      onBackButtonClicked()
    }
    // Guided Tour buttons
    findViewById<android.view.View>(R.id.btn_start_tour)?.setOnClickListener {
      renderer.startGuidedTour()
      Toast.makeText(this, "ℹ️ Recorrido guiado iniciado", Toast.LENGTH_SHORT).show()
    }

    findViewById<android.view.View>(R.id.btn_next_poi)?.setOnClickListener {
      renderer.nextPoi()
    }

    // Eliminado: btn_more_info. Usamos btn_info.

    findViewById<android.view.View>(R.id.btn_finish_tour)?.setOnClickListener {
      // At last POI: switch to return phase instead of immediate finish
      renderer.startReturnPhase()
      Toast.makeText(this, "↩️ Regresa al punto de inicio para completar", Toast.LENGTH_SHORT).show()
    }

    // Flag completion: finalize after returning to start
    findViewById<android.view.View>(R.id.btn_flag_complete)?.setOnClickListener {
      renderer.finishTour()
      Toast.makeText(this, "✅ Has finalizado el recorrido en el punto de inicio", Toast.LENGTH_LONG).show()
    }
  }

  // =============== CONFIGURACIÓN DEL OVERLAY DE INSTRUCCIONES ===============
  private fun setupInstructionsOverlay() {
    // Botón para cerrar las instrucciones
    findViewById<ImageButton>(R.id.btn_close_instructions)?.setOnClickListener {
      HapticFeedback.navigationTap(this)
      hideInstructionsOverlay()
    }
    
    // Cerrar tocando el overlay (fondo)
    instructionsOverlay.setOnClickListener {
      HapticFeedback.navigationTap(this)
      hideInstructionsOverlay()
    }
  }

  fun showInstructionsOverlay() {
    instructionsOverlay.visibility = View.VISIBLE
    Log.d(TAG, "Instructions overlay shown")
  }

  private fun hideInstructionsOverlay() {
    instructionsOverlay.visibility = View.GONE
    Log.d(TAG, "Instructions overlay hidden")
  }

  override fun onBackPressed() {
    // Si las instrucciones están abiertas, cerrarlas primero
    if (instructionsOverlay.visibility == View.VISIBLE) {
      HapticFeedback.navigationTap(this)
      hideInstructionsOverlay()
      return
    }
    
    // Comportamiento normal del botón atrás
    super.onBackPressed()
  }

  // Método de botón AR Tour eliminado

  private fun onInfoButtonClicked() {
    Log.d(TAG, "Info button clicked")
    // Mostrar información general del sitio arqueológico
    Toast.makeText(this, "ℹ️ Información de Kaminaljuyú", Toast.LENGTH_SHORT).show()
  }

  private fun onBackButtonClicked() {
    Log.d(TAG, "Back button clicked")
    // Confirmar salida o regresar al menú principal
    Toast.makeText(this, "↩️ Saliendo del AR", Toast.LENGTH_SHORT).show()
    
    // Opcional: mostrar diálogo de confirmación
    finish() // Sale de la actividad AR
  }

  // Configure the session, setting the desired options according to the usecase.
  fun configureSession(session: Session) {
    session.configure(
      session.config.apply {
        // Only enable Geospatial Mode if supported
        if (session.isGeospatialModeSupported(Config.GeospatialMode.ENABLED)) {
          geospatialMode = Config.GeospatialMode.ENABLED
        } else {
          // Consider implementing a fallback mode here
          Log.w(TAG, "Geospatial mode not supported on this device")
        }
      }
    )
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String>,
    results: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, results)
    if (!GeoPermissionsHelper.hasGeoPermissions(this)) {
      // Use toast instead of snackbar here since the activity will exit.
      Toast.makeText(this, "Camera and location permissions are needed to run this application", Toast.LENGTH_LONG)
        .show()
      if (!GeoPermissionsHelper.shouldShowRequestPermissionRationale(this)) {
        // Permission denied with checking "Do not ask again".
        GeoPermissionsHelper.launchPermissionSettings(this)
      }
      finish()
    }
  }

  override fun onWindowFocusChanged(hasFocus: Boolean) {
    super.onWindowFocusChanged(hasFocus)
    FullScreenHelper.setFullScreenOnWindowFocusChanged(this, hasFocus)
  }
}
