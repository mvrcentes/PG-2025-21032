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
package com.google.ar.core.codelabs.hellogeospatial.helpers

import android.opengl.GLSurfaceView
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import android.graphics.Color
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.ar.core.Earth
import com.google.ar.core.GeospatialPose
import com.google.ar.core.codelabs.hellogeospatial.HelloGeoActivity
import com.google.ar.core.codelabs.hellogeospatial.R
import com.google.ar.core.examples.java.common.helpers.SnackbarHelper
import com.google.ar.core.codelabs.hellogeospatial.models.POILocation

import android.util.Log

/** Contains UI elements for Hello Geo. */
class HelloGeoView(val activity: HelloGeoActivity) : DefaultLifecycleObserver {
  val root = View.inflate(activity, R.layout.activity_ar, null)
  val surfaceView = root.findViewById<GLSurfaceView>(R.id.surfaceview).apply {
    // Set up touch listener for AR surface
    setOnTouchListener { _, event ->
      if (event.action == MotionEvent.ACTION_UP) {
        // Use ACTION_UP for better tap detection
        activity.renderer.onScreenTap(event.x, event.y)
      }
      true
    }
  }
  val TAG = "HelloGeoRenderer"
  val session
    get() = activity.arCoreSessionHelper.session

  val snackbarHelper = SnackbarHelper()

  var mapView: MapView? = null
  val mapTouchWrapper = root.findViewById<MapTouchWrapper>(R.id.map_wrapper).apply {
    setup { screenLocation ->
      val latLng: LatLng =
        mapView?.googleMap?.projection?.fromScreenLocation(screenLocation) ?: return@setup

        activity.renderer.onMapClick(latLng)
    }
  }
  val mapFragment =
    (activity.supportFragmentManager.findFragmentById(R.id.map)!! as SupportMapFragment).also {
      it.getMapAsync { googleMap -> mapView = MapView(activity, googleMap) }
    }

  val statusText = root.findViewById<TextView>(R.id.statusText)
  // Debounce cache for status updates to avoid flicker
  private var lastStatusText: String? = null
  private var lastStatusBg: Int? = null

  private fun setStatusSafely(text: String, bgColor: Int) {
    if (text == lastStatusText && bgColor == lastStatusBg) return
    lastStatusText = text
    lastStatusBg = bgColor
    statusText.text = text
    statusText.setBackgroundColor(bgColor)
  }

  // Guided tour buttons
  private val btnStartTour = root.findViewById<View>(R.id.btn_start_tour)
  private val btnNextPoi = root.findViewById<View>(R.id.btn_next_poi)
  private val btnInfo = root.findViewById<View>(R.id.btn_info)
  private val btnFinishTour = root.findViewById<View>(R.id.btn_finish_tour)
  private val btnFlagComplete = root.findViewById<View>(R.id.btn_flag_complete)
  
  // Helper to visually indicate enabled/disabled state
  private fun setEnabledWithAlpha(view: View, enabled: Boolean) {
    view.isEnabled = enabled
    // Slightly transparent when disabled for clear visual feedback
    view.alpha = if (enabled) 1.0f else 0.4f
  }

  fun setGuidedButtonsState(
    startVisible: Boolean,
    startEnabled: Boolean,
    nextVisible: Boolean,
    nextEnabled: Boolean,
    infoVisible: Boolean,
    infoEnabled: Boolean,
    finishVisible: Boolean,
    finishEnabled: Boolean,
    flagVisible: Boolean,
    flagEnabled: Boolean
  ) {
    btnStartTour.visibility = if (startVisible) View.VISIBLE else View.GONE
    setEnabledWithAlpha(btnStartTour, startEnabled)
    btnNextPoi.visibility = if (nextVisible) View.VISIBLE else View.GONE
    setEnabledWithAlpha(btnNextPoi, nextEnabled)
  btnInfo.visibility = if (infoVisible) View.VISIBLE else View.GONE
  setEnabledWithAlpha(btnInfo, infoEnabled)
    btnFinishTour.visibility = if (finishVisible) View.VISIBLE else View.GONE
    setEnabledWithAlpha(btnFinishTour, finishEnabled)
    btnFlagComplete.visibility = if (flagVisible) View.VISIBLE else View.GONE
    setEnabledWithAlpha(btnFlagComplete, flagEnabled)
  }
  fun updateReturnStatus(message: String) {
    activity.runOnUiThread { setStatusSafely(message, 0xAA2196F3.toInt()) }
  }

  fun showToast(message: String) {
    android.widget.Toast.makeText(activity, message, android.widget.Toast.LENGTH_SHORT).show()
  }
  fun updateStatusText(earth: Earth, cameraGeospatialPose: GeospatialPose?) {
    activity.runOnUiThread {
      val poseText = if (cameraGeospatialPose == null) "" else
        activity.getString(R.string.geospatial_pose,
                           cameraGeospatialPose.latitude,
                           cameraGeospatialPose.longitude,
                           cameraGeospatialPose.horizontalAccuracy,
                           cameraGeospatialPose.altitude,
                           cameraGeospatialPose.verticalAccuracy,
                           cameraGeospatialPose.heading,
                           cameraGeospatialPose.headingAccuracy)
      statusText.text = activity.resources.getString(R.string.earth_state,
                                                     earth.earthState.toString(),
                                                     earth.trackingState.toString(),
                                                     poseText)
    }
  }
  
  // Update status text with proximity information for pyramids
  fun updateProximityInfo(nearbyPoi: POILocation?, distance: Double?) {
    activity.runOnUiThread {
      val proximityText = when {
        nearbyPoi != null && distance != null && distance <= 30.0 -> "📍 ${nearbyPoi.name} muy cerca (${distance.toInt()}m)\nPulsa el botón de información"
        nearbyPoi != null && distance != null && distance <= 50.0 -> "📍 ${nearbyPoi.name} cerca (${distance.toInt()}m)\nUsa el botón de información"
        nearbyPoi != null && distance != null && distance <= 100.0 -> "🔍 ${nearbyPoi.name} visible (${distance.toInt()}m)\nAcércate para ver detalles"
        else -> "🗺️ Explora el área para encontrar montículos arqueológicos"
      }
      val bg = when {
        nearbyPoi != null && distance != null && distance <= 50.0 -> 0xAA4CAF50.toInt() // Green
        nearbyPoi != null && distance != null && distance <= 100.0 -> 0xAAFF9800.toInt() // Orange
        else -> 0xAAFFFFFF.toInt() // White
      }
      setStatusSafely(proximityText, bg)
    }
  }
  
  // Adds a marker for a POI location on the map.
  fun addMarkerForPOI(poi: POILocation) {
    Log.d(TAG, "Adding marker for POI: ${poi.name} at ${poi.position}")
    activity.runOnUiThread {
      val marker = mapView?.googleMap?.addMarker(
        MarkerOptions()
          .position(poi.position)
          .title(poi.name)
      )
      if (marker != null) poiMarkers[poi.id] = marker
    }
  }

  // Map of poi id to its marker
  private val poiMarkers = mutableMapOf<String, Marker>()

  // Golden route color (from app palette/image). Adjust if needed.
  private val ROUTE_COLOR: Int = Color.parseColor("#E8B86B") // ARGB: FF E8 B8 6B

  // Route polyline reference for updating/clearing
  private var routePolyline: Polyline? = null

  fun setRoutePolyline(points: List<LatLng>, color: Int = ROUTE_COLOR, width: Float = 8f) {
    activity.runOnUiThread {
      routePolyline?.remove()
      routePolyline = null
      if (points.size >= 2) {
        routePolyline = mapView?.googleMap?.addPolyline(
          PolylineOptions().addAll(points).color(color).width(width)
        )
      }
    }
  }

  fun clearRoutePolyline() {
    activity.runOnUiThread {
      routePolyline?.remove()
      routePolyline = null
    }
  }

  // Connector points feature reverted; no dot markers are drawn

  

  /**
   * Highlight one marker and attenuate others. If currentId is null, attenuate all.
   * When returningPhase = true, all are attenuated (focus shifts to start point guidance).
   */
  fun updatePoiMarkerHighlight(currentId: String?, returningPhase: Boolean) {
    activity.runOnUiThread {
      poiMarkers.forEach { (id, marker) ->
        val alpha = when {
          returningPhase -> 0.25f
          currentId == null -> 0.5f
          id == currentId -> 1.0f
          else -> 0.35f
        }
        marker.alpha = alpha
      }
    }
  }

  override fun onResume(owner: LifecycleOwner) {
    surfaceView.onResume()
  }

  override fun onPause(owner: LifecycleOwner) {
    surfaceView.onPause()
  }

  // Loading overlay controls
  private val loadingOverlay = root.findViewById<View>(R.id.loading_overlay)
  private val loadingMessage = root.findViewById<TextView>(R.id.loading_message)
  fun setLoadingState(visible: Boolean, message: String? = null) {
    activity.runOnUiThread {
      loadingOverlay.visibility = if (visible) View.VISIBLE else View.GONE
      message?.let { loadingMessage.text = it }
    }
  }
}
