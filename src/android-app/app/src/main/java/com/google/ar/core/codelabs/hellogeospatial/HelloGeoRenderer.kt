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
import android.opengl.Matrix
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.maps.model.LatLng
import com.google.ar.core.Anchor
import com.google.ar.core.Earth
import com.google.ar.core.GeospatialPose
import com.google.ar.core.Session
import com.google.ar.core.TrackingState
import com.google.ar.core.examples.java.common.helpers.DisplayRotationHelper
import com.google.ar.core.examples.java.common.helpers.TrackingStateHelper
import com.google.ar.core.examples.java.common.samplerender.Framebuffer
import com.google.ar.core.examples.java.common.samplerender.Mesh
import com.google.ar.core.examples.java.common.samplerender.SampleRender
import com.google.ar.core.examples.java.common.samplerender.Shader
import com.google.ar.core.examples.java.common.samplerender.Texture
import com.google.ar.core.examples.java.common.samplerender.arcore.BackgroundRenderer
import com.google.ar.core.exceptions.CameraNotAvailableException
import java.io.IOException
import java.util.HashMap

import com.google.ar.core.codelabs.hellogeospatial.models.POILocation
import com.google.ar.core.codelabs.hellogeospatial.utilities.calculateDistanceInMeters
import com.google.ar.core.codelabs.hellogeospatial.helpers.PyramidInfoDialog
import kotlin.math.sin
import kotlin.math.PI
import com.google.ar.core.codelabs.hellogeospatial.navigation.RouteNavigator
import com.google.ar.core.codelabs.hellogeospatial.ui.ArrowIndicator
import com.google.ar.core.codelabs.hellogeospatial.utilities.bearingBetweenDeg
import com.google.ar.core.codelabs.hellogeospatial.utilities.normalizeDeg180
import com.google.ar.core.codelabs.hellogeospatial.engine.EarthObjectManager
import com.google.ar.core.codelabs.hellogeospatial.config.HelloGeoConfig
import com.google.ar.core.codelabs.hellogeospatial.utilities.buildStartLeg
import com.google.ar.core.codelabs.hellogeospatial.utilities.buildReturnLeg
import com.google.ar.core.codelabs.hellogeospatial.utilities.buildFullRouteIncludingStart
import com.google.ar.core.codelabs.hellogeospatial.utilities.connectorsForStartLeg
import com.google.ar.core.codelabs.hellogeospatial.utilities.connectorsForPoiLeg
import com.google.ar.core.codelabs.hellogeospatial.utilities.connectorsForReturnLeg
import com.google.ar.core.codelabs.hellogeospatial.utilities.getPoiLocations


class HelloGeoRenderer(val activity: HelloGeoActivity) :
  SampleRender.Renderer, DefaultLifecycleObserver {
  //<editor-fold desc="ARCore initialization" defaultstate="collapsed">


  //ROTACION
  var modelRotationY = 0f

  fun updateRotation(deltaDegrees: Float) {
    modelRotationY = (modelRotationY + deltaDegrees) % 360f
  }

  //ZOOM
  var modelScale = 1f

  // —— Ruta y flecha indicador ——
  private lateinit var routeNavigator: RouteNavigator
  private lateinit var arrowIndicator: ArrowIndicator
  private var showArrow = false
  // Guided leg navigator (uses connectors between current and next POI)
  private var guidedLegNavigator: RouteNavigator? = null
  // Start leg navigator (START -> first POI)
  private var startLegNavigator: RouteNavigator? = null
  // Return leg navigator (last POI -> START)
  private var returnLegNavigator: RouteNavigator? = null

  // —— Guided tour state ——
  private var guidedTourActive = false
  private var guidedCurrentIndex = 0
  private var poiList: List<POILocation> = emptyList()
  private var arrivalThresholdMeters = HelloGeoConfig.GUIDED_ARRIVAL_METERS
  // Return-to-start phase after last POI
  private var returningToStart = false
  
  // —— Instructions state ——
  private var instructionsShown = false

  fun startGuidedTour() {
    // Validate start radius before starting the tour
    val earth = session?.earth ?: return
    if (earth.trackingState != TrackingState.TRACKING) return
    val cam = earth.cameraGeospatialPose
    val distToStart = calculateDistanceInMeters(
      cam.latitude, cam.longitude,
      HelloGeoConfig.TOUR_START_LAT, HelloGeoConfig.TOUR_START_LON
    )
    if (distToStart > HelloGeoConfig.TOUR_START_RADIUS_METERS) {
      activity.view.snackbarHelper.showMessage(activity, "Acércate al punto de inicio para comenzar el recorrido")
      return
    }

    poiList = getPoiLocations(activity)
    if (poiList.isEmpty()) return
    guidedTourActive = true
    guidedCurrentIndex = 0
  returningToStart = false
    showArrow = true
    // Draw full guided route including START->first connectors on the map (visual only)
    val startLatLng = LatLng(HelloGeoConfig.TOUR_START_LAT, HelloGeoConfig.TOUR_START_LON)
    val fullRoute = buildFullRouteIncludingStart(startLatLng, poiList)
    activity.view.setRoutePolyline(fullRoute)
    // Show connector dots for the visible full route (exclude POI points)
    // We draw dots for all connectors that exist in the configured legs
    val connectorDots = fullRoute.filter { point ->
      // A simple heuristic: drop POI positions (match against list) and keep in-between
      // Build a set of POI positions for quick lookup
      false
    }
    // For now, we will just show connectors when we build each leg (start/return/poi->poi)
    // Prepare START->first leg so connectors auto-advance; then wait at the first POI until you press "Siguiente"
    startLegNavigator = if (poiList.isNotEmpty()) {
      val connectorsOnly = connectorsForStartLeg(poiList.first())
      if (connectorsOnly.isNotEmpty()) RouteNavigator(connectorsOnly, HelloGeoConfig.CONNECTOR_ARRIVAL_METERS) else null
    } else null
    // Reset model transforms at tour start for a clean view
    modelScale = 1f
    modelRotationY = 0f
    // Highlight first POI marker
    activity.view.updatePoiMarkerHighlight(poiList[guidedCurrentIndex].id, returningPhase = false)
  }

  fun nextPoi() {
    if (!guidedTourActive) return
    if (returningToStart) return // no next while returning
    if (guidedCurrentIndex < poiList.lastIndex) {
      val fromIndex = guidedCurrentIndex
      guidedCurrentIndex++
      // Reset model transforms when switching to the next mound
      modelScale = 1f
      modelRotationY = 0f
  // Start leg navigation from previous POI to the new current POI (connectors auto-advance)
  rebuildGuidedLegNavigator(fromIndex, guidedCurrentIndex)
  // No connector dots on the map (reverted)
      activity.view.updatePoiMarkerHighlight(poiList[guidedCurrentIndex].id, returningPhase = false)
    }
  }

  fun finishTour() {
    guidedTourActive = false
    showArrow = false
    returningToStart = false
    guidedLegNavigator = null
    startLegNavigator = null
    returnLegNavigator = null
    // Restore default transforms when leaving guided mode
    modelScale = 1f
    modelRotationY = 0f
    // Reset marker highlighting
    activity.view.updatePoiMarkerHighlight(null, returningPhase = false)
    // Clear route line
    activity.view.clearRoutePolyline()
  }

  // Called when user presses the finish button at the last POI (does not end tour yet)
  fun startReturnPhase() {
    if (!guidedTourActive) return
    if (returningToStart) return
    if (guidedCurrentIndex != poiList.lastIndex) return
    // Enter return-to-start phase; arrow now points to start
    returningToStart = true
    guidedLegNavigator = null
    startLegNavigator = null
    // Build and draw return leg with connectors from last POI to START
    if (poiList.isNotEmpty()) {
      val lastPoi = poiList[guidedCurrentIndex]
      val startLatLng = LatLng(HelloGeoConfig.TOUR_START_LAT, HelloGeoConfig.TOUR_START_LON)
      // For navigation: only connectors so START/POI are not treated as connector targets
      val returnConnectors = connectorsForReturnLeg(lastPoi)
      if (returnConnectors.isNotEmpty()) {
        returnLegNavigator = RouteNavigator(returnConnectors, HelloGeoConfig.CONNECTOR_ARRIVAL_METERS)
      } else {
        returnLegNavigator = null
      }
      // For visuals: draw the full return leg including endpoints
      val returnLegPath = buildReturnLeg(lastPoi, startLatLng)
      if (returnLegPath.size >= 2) {
        activity.view.setRoutePolyline(returnLegPath)
      }
    }
    // Reset transforms for clarity
    modelScale = 1f
    modelRotationY = 0f
    // Attenuate all markers in return phase
    activity.view.updatePoiMarkerHighlight(null, returningPhase = true)
    activity.runOnUiThread {
      activity.view.showToast("Regresa al punto de inicio para finalizar")
    }
  }



  fun updateScale(scaleFactor: Float) {
    modelScale = (modelScale * scaleFactor)
      .coerceIn(HelloGeoConfig.MIN_SCALE, HelloGeoConfig.MAX_SCALE)
  }

  companion object {
    val TAG = "HelloGeoRenderer"

  private val Z_NEAR = HelloGeoConfig.Z_NEAR
  private val Z_FAR = HelloGeoConfig.Z_FAR
  }

  lateinit var backgroundRenderer: BackgroundRenderer
  lateinit var virtualSceneFramebuffer: Framebuffer
  var hasSetTextureNames = false

  // Virtual object (ARCore pawn)
  lateinit var virtualObjectMesh: Mesh
  lateinit var virtualObjectShader: Shader
  lateinit var virtualObjectTexture: Texture

  // Temporary matrix allocated here to reduce number of allocations for each frame.
  val modelMatrix = FloatArray(16)
  val viewMatrix = FloatArray(16)
  val projectionMatrix = FloatArray(16)
  val modelViewMatrix = FloatArray(16) // view x model

  val modelViewProjectionMatrix = FloatArray(16) // projection x view x model

  val session
    get() = activity.arCoreSessionHelper.session

  val displayRotationHelper = DisplayRotationHelper(activity)
  val trackingStateHelper = TrackingStateHelper(activity)

  // Anchor/model/texture manager
  private lateinit var earthObjects: EarthObjectManager
  lateinit var defaultObjectShader: Shader
  lateinit var litObjectShader: Shader

  // Info dialog for pyramids
  private lateinit var pyramidInfoDialog: PyramidInfoDialog

  // Interaction detection
  private var lastTapTime = 0L
  // Map markers control
  private var markersAdded = false

  fun showCurrentPoiInfoIfClose() {
    if (!guidedTourActive || poiList.isEmpty()) return
    val earth = session?.earth ?: return
    if (earth.trackingState != TrackingState.TRACKING) return
    val cameraPose = earth.cameraGeospatialPose
    val currentPoi = poiList[guidedCurrentIndex]
    val dist = calculateDistanceInMeters(cameraPose.latitude, cameraPose.longitude, currentPoi.position.latitude, currentPoi.position.longitude)
    if (dist <= arrivalThresholdMeters) {
      activity.runOnUiThread { pyramidInfoDialog.show(currentPoi) }
    }
  }


  override fun onResume(owner: LifecycleOwner) {
    displayRotationHelper.onResume()
    hasSetTextureNames = false
  }

  override fun onPause(owner: LifecycleOwner) {
    displayRotationHelper.onPause()
  }

  override fun onSurfaceCreated(render: SampleRender) {
    // Prepare the rendering objects.
    // This involves reading shaders and 3D model files, so may throw an IOException.
    try {
      backgroundRenderer = BackgroundRenderer(render)
      virtualSceneFramebuffer = Framebuffer(render, /*width=*/ 1, /*height=*/ 1)

  // Load default shader first (used by all models)
      defaultObjectShader = Shader.createFromAssets(
        render,
        "shaders/ar_unlit_object.vert",
        "shaders/ar_unlit_object.frag",
        /*defines=*/ null
      )


  // Ruta: usa las coordenadas de tus POIs como recorrido
      val routePoints = getPoiLocations(activity).map { it.position }
      routeNavigator = RouteNavigator(
        waypoints = routePoints,
        arrivalThresholdMeters = HelloGeoConfig.GUIDED_ARRIVAL_METERS
      )

      // Flecha HUD: usa flecha.obj con su propio shader/texture para evitar interferencias
      val arrowShader = Shader.createFromAssets(
        render,
        "shaders/ar_unlit_object.vert",
        "shaders/ar_unlit_object.frag",
        /*defines=*/ null
      )
      arrowIndicator = ArrowIndicator(
        render = render,
        defaultShader = arrowShader,
        framebuffer = virtualSceneFramebuffer,
        modelPath = "models/arrowGuia.obj",
        texturePath = "models/arrowTexture.png", // usar siempre la textura de la flecha
        hudDistanceMeters = 5.2f,
        hudYOffsetMeters = -0.10f,
        modelYawOffsetDeg = -90f,   // ejemplo si la flecha mira a +X
        modelPitchOffsetDeg = 0f,
        modelRollOffsetDeg = 0f      // ajusta si tu flecha no apunta “de frente”
      )



  // Set up Earth objects manager (models, textures, anchors)
  earthObjects = EarthObjectManager(activity, render, defaultObjectShader)
  earthObjects.ensureAssetsLoaded()

  // Initialize default objects for backward compatibility
      virtualObjectMesh = Mesh.createFromAsset(render, "models/piramide_test.obj")

      virtualObjectTexture = Texture.createFromAsset(
        render,
        "models/arrowTexture.png",
        Texture.WrapMode.CLAMP_TO_EDGE,
        Texture.ColorFormat.SRGB
      )

      // Load simple lit shader (ambient + directional)
      litObjectShader = Shader.createFromAssets(
        render,
        "shaders/ar_simple_lit.vert",
        "shaders/ar_simple_lit.frag",
        /*defines=*/ null
      )
      virtualObjectShader = litObjectShader.setTexture("u_Texture", virtualObjectTexture)

      backgroundRenderer.setUseDepthVisualization(render, false)
      backgroundRenderer.setUseOcclusion(render, false)

      // Initialize pyramid info dialog
      pyramidInfoDialog = PyramidInfoDialog(activity)
    } catch (e: IOException) {
      Log.e(TAG, "Failed to read a required asset file", e)
      showError("Failed to read a required asset file: $e")
    }
  }

  override fun onSurfaceChanged(render: SampleRender, width: Int, height: Int) {
    displayRotationHelper.onSurfaceChanged(width, height)
    virtualSceneFramebuffer.resize(width, height)
  }
  //</editor-fold>

  override fun onDrawFrame(render: SampleRender) {
    val session :Session = session ?: return

    //<editor-fold desc="ARCore frame boilerplate" defaultstate="collapsed">
    // Texture names should only be set once on a GL thread unless they change. This is done during
    // onDrawFrame rather than onSurfaceCreated since the session is not guaranteed to have been
    // initialized during the execution of onSurfaceCreated.
    if (!hasSetTextureNames) {
      session.setCameraTextureNames(intArrayOf(backgroundRenderer.cameraColorTexture.textureId))
      hasSetTextureNames = true
    }

    // -- Update per-frame state

    // Notify ARCore session that the view size changed so that the perspective matrix and
    // the video background can be properly adjusted.
    displayRotationHelper.updateSessionIfNeeded(session)

    // Obtain the current frame from ARSession. When the configuration is set to
    // UpdateMode.BLOCKING (it is by default), this will throttle the rendering to the
    // camera framerate.
    val frame =
      try {
        session.update()
      } catch (e: CameraNotAvailableException) {
        Log.e(TAG, "Camera not available during onDrawFrame", e)
        showError("Camera not available. Try restarting the app.")
        return
      }

    val camera = frame.camera

    // BackgroundRenderer.updateDisplayGeometry must be called every frame to update the coordinates
    // used to draw the background camera image.
    backgroundRenderer.updateDisplayGeometry(frame)

    // Keep the screen unlocked while tracking, but allow it to lock when tracking stops.
    trackingStateHelper.updateKeepScreenOnFlag(camera.trackingState)

    // -- Draw background
    if (frame.timestamp != 0L) {
      // Suppress rendering if the camera did not produce the first frame yet. This is to avoid
      // drawing possible leftover data from previous sessions if the texture is reused.
      try {
        backgroundRenderer.drawBackground(render)
      } catch (e: NullPointerException) {
        // Some devices may hit a rare race where the internal background shader isn't ready yet.
        // Skip this frame and let it initialize.
        Log.w(TAG, "Background renderer not ready, skipping frame", e)
      }
    }

    // If not tracking, don't draw 3D objects.
    if (camera.trackingState == TrackingState.PAUSED) {
      return
    }

    // Get projection matrix.
    camera.getProjectionMatrix(projectionMatrix, 0, Z_NEAR, Z_FAR)

    // Get camera matrix and draw.
    camera.getViewMatrix(viewMatrix, 0)

    render.clear(virtualSceneFramebuffer, 0f, 0f, 0f, 0f)
    //</editor-fold>

    // TODO: Obtain Geospatial information and display it on the map.
  val earth :Earth = session.earth ?: return
    lateinit var cameraGeospatialPose : GeospatialPose

    if (earth.trackingState == TrackingState.TRACKING) {
      // Place predefined objects only once when Earth is tracking
  cameraGeospatialPose = earth.cameraGeospatialPose
  earthObjects.placeAllIfNeeded(earth)
      if (!markersAdded && earthObjects.allAnchors().isNotEmpty()) {
        for (poi in earthObjects.pois()) activity.view.addMarkerForPOI(poi)
        markersAdded = true
        // If tour already active highlight current else attenuate all
        if (guidedTourActive && poiList.isNotEmpty()) {
          activity.view.updatePoiMarkerHighlight(poiList[guidedCurrentIndex].id, returningPhase = returningToStart)
        } else {
          activity.view.updatePoiMarkerHighlight(null, returningPhase = false)
        }
      }

      activity.view.mapView?.updateMapPosition(
        latitude = cameraGeospatialPose.latitude,
        longitude = cameraGeospatialPose.longitude,
        heading = cameraGeospatialPose.heading
      )
      // Overlay: hide once UI components are placed/ready under tracking
      activity.view.setLoadingState(false)
      
      // Show instructions overlay only once after loading is complete
      if (!instructionsShown) {
        instructionsShown = true
        activity.showInstructionsOverlay()
      }
    } else {
  activity.view.setLoadingState(true, "Inicializando AR...")
    // Reset the flag if tracking is lost, so objects and markers can be placed again if needed
    earthObjects.clear()
    markersAdded = false
    return
    }

    // Note: overlay no longer depends on accuracy/heading thresholds

    //activity.view.updateStatusText(earth, earth.cameraGeospatialPose)

    // Guided/Free rendering: only render current POI in guided mode
    var nearestPoi: POILocation? = null
    var nearestDistance: Double = Double.MAX_VALUE

    val allAnchors = earthObjects.allAnchors()
    val allPois = earthObjects.pois()

  val currentGuidedPoi: POILocation? = if (guidedTourActive && poiList.isNotEmpty()) poiList[guidedCurrentIndex] else null
  val renderGuidedPoi = guidedTourActive && !returningToStart

    for ((id, anchor) in allAnchors) {
      val poi = allPois.find { it.id == id } ?: continue

      val distance = calculateDistanceInMeters(
        cameraGeospatialPose.latitude, cameraGeospatialPose.longitude,
        poi.position.latitude, poi.position.longitude
      )

      if (!guidedTourActive) {
        // Guided mode (pre-start): do not render any pyramids before pressing "Iniciar recorrido".
        // Skip rendering and proximity tracking here.
      } else {
        // Guided: while returningToStart, hide all POIs (do not render last one)
        if (renderGuidedPoi) {
          if (currentGuidedPoi != null && poi.id == currentGuidedPoi.id) {
            if (distance < nearestDistance) {
              nearestDistance = distance
              nearestPoi = poi
            }
            render.renderCompassAtAnchor(anchor, poi)
          }
        }
      }
    }

    // Update proximity UI: before starting guided tour, don't show proximity info either
    val showPoi = if (guidedTourActive && !returningToStart) currentGuidedPoi else null
    val showDist = if (guidedTourActive && !returningToStart && nearestPoi?.id == currentGuidedPoi?.id) nearestDistance else null

    if (!(guidedTourActive && returningToStart)) {
      activity.view.updateProximityInfo(showPoi, showDist)
    }

    val cameraLat = cameraGeospatialPose.latitude
    val cameraLon = cameraGeospatialPose.longitude
    val cameraHeadingDeg = cameraGeospatialPose.heading


    // Arrow logic: point to current guided target or nearest in free mode via RouteNavigator
    val navTarget = if (guidedTourActive && poiList.isNotEmpty()) {
      if (returningToStart) {
        val ret = returnLegNavigator
        if (ret != null) {
          val legTarget = ret.updateAndGetTarget(cameraLat, cameraLon)
          if (ret.isFinished()) {
            returnLegNavigator = null
          }
          legTarget ?: LatLng(HelloGeoConfig.TOUR_START_LAT, HelloGeoConfig.TOUR_START_LON)
        } else {
          LatLng(HelloGeoConfig.TOUR_START_LAT, HelloGeoConfig.TOUR_START_LON)
        }
      } else {
        // Priority: START leg during initial segment -> guided leg (POI->POI) -> current POI
        val startLeg = startLegNavigator
        if (startLeg != null) {
          val legTarget = startLeg.updateAndGetTarget(cameraLat, cameraLon)
          if (startLeg.isFinished()) {
            startLegNavigator = null
          }
          legTarget ?: poiList[guidedCurrentIndex].position
        } else {
          val leg = guidedLegNavigator
          if (leg != null) {
            val legTarget = leg.updateAndGetTarget(cameraLat, cameraLon)
            // If the leg finished (reached final target), stop auto-advance; wait for Next at POI
            if (leg.isFinished()) {
              guidedLegNavigator = null
            }
            legTarget ?: poiList[guidedCurrentIndex].position
          } else {
            poiList[guidedCurrentIndex].position
          }
        }
      }
    } else {
      // Free mode keeps using basic waypoint list (no connectors)
      routeNavigator.updateAndGetTarget(cameraLat, cameraLon)
    }

    if (showArrow && navTarget != null) {
      val distance = calculateDistanceInMeters(cameraLat, cameraLon, navTarget.latitude, navTarget.longitude)
      val bearing  = bearingBetweenDeg(cameraLat, cameraLon, navTarget.latitude, navTarget.longitude)
      val heading  = cameraGeospatialPose.heading
      val delta    = normalizeDeg180(bearing - heading)

      Log.d(TAG, "ARROW ▶  dist=${"%.1f".format(distance)}m bearing=${"%.1f".format(bearing)}° heading=${"%.1f".format(heading)}° delta=${"%.1f".format(delta)}°")

      arrowIndicator.draw(
        render = render,
        viewMatrix = viewMatrix,
        projectionMatrix = projectionMatrix,
        cameraLat = cameraLat,
        cameraLon = cameraLon,
        cameraHeadingDeg = heading,
        targetLat = navTarget.latitude,
        targetLon = navTarget.longitude
      )
    }

    // Enable/disable UI buttons based on guided tour state and proximity
    if (guidedTourActive) {
      if (!returningToStart) {
        val currentPoi = poiList[guidedCurrentIndex]
        val dist = calculateDistanceInMeters(
          cameraLat, cameraLon,
          currentPoi.position.latitude, currentPoi.position.longitude
        )
        activity.runOnUiThread {
          activity.view.setGuidedButtonsState(
            startVisible = false,
            startEnabled = false,
            nextVisible = guidedCurrentIndex < poiList.lastIndex,
            nextEnabled = dist <= arrivalThresholdMeters,
            infoVisible = true,
            infoEnabled = dist <= arrivalThresholdMeters,
            finishVisible = guidedCurrentIndex == poiList.lastIndex,
            finishEnabled = dist <= arrivalThresholdMeters,
            flagVisible = false,
            flagEnabled = false
          )
        }
      }
      if (returningToStart) {
        val distToStart = calculateDistanceInMeters(
          cameraLat, cameraLon,
          HelloGeoConfig.TOUR_START_LAT, HelloGeoConfig.TOUR_START_LON
        )
        activity.runOnUiThread {
          activity.view.setGuidedButtonsState(
            startVisible = false,
            startEnabled = false,
            nextVisible = false,
            nextEnabled = false,
            infoVisible = false,
            infoEnabled = false,
            finishVisible = false,
            finishEnabled = false,
            flagVisible = true,
            flagEnabled = distToStart <= HelloGeoConfig.TOUR_START_RADIUS_METERS
          )
        }
        if (distToStart <= HelloGeoConfig.TOUR_START_RADIUS_METERS) {
          activity.runOnUiThread {
            activity.view.updateReturnStatus("🚩 Estás en el punto de inicio. Pulsa la bandera para finalizar.")
          }
        } else {
          activity.runOnUiThread {
            activity.view.updateReturnStatus("↩️ Regresa al punto de inicio (${distToStart.toInt()}m)")
          }
        }
      }
    } else {
      // Outside guided mode: compute eligibility to start based on distance to start point
      val distStart = calculateDistanceInMeters(
        cameraLat, cameraLon,
        HelloGeoConfig.TOUR_START_LAT, HelloGeoConfig.TOUR_START_LON
      )
      val canStart = distStart <= HelloGeoConfig.TOUR_START_RADIUS_METERS
      activity.runOnUiThread {
        activity.view.setGuidedButtonsState(
          startVisible = true,
          startEnabled = canStart,
          nextVisible = false,
          nextEnabled = false,
          infoVisible = false,
          infoEnabled = false,
          finishVisible = false,
          finishEnabled = false,
          flagVisible = false,
          flagEnabled = false
        )
      }
    }


    // Compose the virtual scene with the background.
    backgroundRenderer.drawVirtualScene(render, virtualSceneFramebuffer, Z_NEAR, Z_FAR)
  }

  // Rebuild guided-leg navigator from current POI to next POI including connectors
  private fun rebuildGuidedLegNavigator(fromIndex: Int, toIndex: Int) {
    guidedLegNavigator = null
    if (poiList.isEmpty()) return
    if (fromIndex !in poiList.indices) return
    if (toIndex !in poiList.indices) return
    if (fromIndex == toIndex) return
    // Use only connector waypoints between the two POIs; exclude endpoints so POIs aren't treated as connector targets
    val connectorsOnly = connectorsForPoiLeg(poiList[fromIndex], poiList[toIndex])
    if (connectorsOnly.isNotEmpty()) {
      guidedLegNavigator = RouteNavigator(connectorsOnly, HelloGeoConfig.CONNECTOR_ARRIVAL_METERS)
    }
  }

  // var earthAnchor: Anchor? = null

  fun onMapClick(latLng: LatLng) {
    // TODO: place an anchor at the given position.
    val earth = session?.earth ?: return
    if (earth.trackingState != TrackingState.TRACKING) {
      return
    }

    Log.d(TAG, "Touching anchor at $latLng")
  }

  // Tap on screen no longer opens info; info is shown via btn_info only
  fun onScreenTap(x: Float, y: Float) {
    val currentTime = System.currentTimeMillis()
    if (currentTime - lastTapTime < HelloGeoConfig.TAP_DELAY_MS) return
    lastTapTime = currentTime
    // Tap ignored (info via btn_info only)
  }

  // Check if a screen tap intersects with a 3D pyramid model
  // Tap hit testing moved to TapHitTester

  // Method to place all predefined objects
  // Anchor placement is handled by EarthObjectManager

  private fun SampleRender.renderCompassAtAnchor(anchor: Anchor, poi: POILocation? = null) {
    // Get the current pose of the Anchor in world space. The Anchor pose is updated
    // during calls to session.update() as ARCore refines its estimate of the world.
    anchor.pose.toMatrix(modelMatrix, 0)

    // ——— base altitude offset (site topography correction) ———
    val baseAlt = poi?.baseAltitudeMeters ?: 0f
    if (baseAlt != 0f) {
      Matrix.translateM(
        modelMatrix, 0,
        0f,
        baseAlt,
        0f
      )
    }

    // ——— bobbing (vertical oscillation) ———
    if (poi?.bobbingEnabled == true) {
      // tiempo en segundos (float)
      val t = System.nanoTime() / 1_000_000_000f
      // offset = A * sin(2π * f * t)
      val offsetY = poi.bobbingAmplitude *
              sin(2f * PI.toFloat() * poi.bobbingSpeed * t)
      Matrix.translateM(
        modelMatrix, 0,
        0f,            // no X
        offsetY,       // Y oscilante
        0f             // no Z
      )
    }

    // Apply per-POI base yaw first, then user-controlled yaw for fine tuning
    val baseYaw = poi?.baseYawDeg ?: 0f
    Matrix.rotateM(
      modelMatrix,
      0,
      baseYaw,
      0f, 1f, 0f
    )
    Matrix.rotateM(
      modelMatrix,
      0,
      modelRotationY,
      0f, 1f, 0f
    )

  val effectiveScale = if (poi != null) modelScale * poi.baseScale else modelScale
  Matrix.scaleM(
   modelMatrix,  // matriz a modificar
   0,            // offset
   effectiveScale,   // escala en X
   effectiveScale,   // escala en Y
   effectiveScale    // escala en Z
  )


    // 3. Ahora reconstruye View×Model y Projection×View×Model
    Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0)
    Matrix.multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, modelViewMatrix, 0)

    // 4. Selecciona mesh/textura/shader y dibuja

    // Determine which model(s) and texture(s) to use
    if (poi != null) {
      val parts = earthObjects.meshesFor(poi)
      for ((mesh, texture) in parts) {
        val shader = litObjectShader
          .setTexture("u_Texture", texture)
          .setMat4("u_ModelViewProjection", modelViewProjectionMatrix)
          .setMat4("u_ModelView", modelViewMatrix)
          .setVec3("u_LightDirVS", HelloGeoConfig.LIGHT_DIRECTION_VS)
          .setFloat("u_Ambient", HelloGeoConfig.LIGHT_AMBIENT)
          .setFloat("u_Diffuse", HelloGeoConfig.LIGHT_DIFFUSE)
        draw(mesh, shader, virtualSceneFramebuffer)
      }
    } else {
      val shader = litObjectShader
        .setTexture("u_Texture", virtualObjectTexture)
        .setMat4("u_ModelViewProjection", modelViewProjectionMatrix)
        .setMat4("u_ModelView", modelViewMatrix)
        .setVec3("u_LightDirVS", HelloGeoConfig.LIGHT_DIRECTION_VS)
        .setFloat("u_Ambient", HelloGeoConfig.LIGHT_AMBIENT)
        .setFloat("u_Diffuse", HelloGeoConfig.LIGHT_DIFFUSE)
      draw(virtualObjectMesh, shader, virtualSceneFramebuffer)
    }


    // Compute ModelViewProjection matrix
    Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0)
    Matrix.multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, modelViewMatrix, 0)



    // Update shader properties and draw
  // Second draw (if needed by pipeline) is removed to avoid duplicate draws per part
    
    // Log rendering for debugging
    Log.v(TAG, "Rendered pyramid ${poi?.name ?: "default"} at anchor position")
  }

  private fun showError(errorMessage: String) =
    activity.view.snackbarHelper.showError(activity, errorMessage)
}
