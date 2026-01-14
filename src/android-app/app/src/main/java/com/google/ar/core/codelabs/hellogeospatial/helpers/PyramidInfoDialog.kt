package com.google.ar.core.codelabs.hellogeospatial.helpers

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Matrix
import android.graphics.PointF
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.ar.core.codelabs.hellogeospatial.R
import com.google.ar.core.codelabs.hellogeospatial.models.POILocation
import kotlin.math.max
import kotlin.math.min

class PyramidInfoDialog(private val context: Context) {

    private var dialog: Dialog? = null

    fun show(poi: POILocation) {
        dismiss() // Close any existing dialog

        dialog = Dialog(context).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_pyramid_info, null)
            setContentView(dialogView)

            // Make dialog cancelable
            setCancelable(true)
            setCanceledOnTouchOutside(true)

            // Set dialog size
            window?.setLayout(
                (context.resources.displayMetrics.widthPixels * 0.9).toInt(),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            // Populate data
            populateViews(dialogView, poi)

            // Set up click listeners
            setupClickListeners(dialogView, poi)

            show()
        }
    }

    private fun populateViews(dialogView: View, poi: POILocation) {
        // Title is always shown
        dialogView.findViewById<TextView>(R.id.pyramidTitle)?.text = poi.name

        // Show/hide sections based on content
        setSectionTextOrHide(dialogView, R.id.descriptionSection, R.id.pyramidDescription, poi.description)
        setSectionTextOrHide(dialogView, R.id.historySection, R.id.pyramidHistory, poi.historicalInfo)
        setSectionTextOrHide(dialogView, R.id.periodSection, R.id.pyramidPeriod, poi.constructionPeriod)
        setSectionTextOrHide(dialogView, R.id.heightSection, R.id.pyramidHeight, poi.height)
        // featuresSection removed to avoid duplication with periodSection
        dialogView.findViewById<View>(R.id.featuresSection)?.visibility = View.GONE
        setSectionTextOrHide(dialogView, R.id.significanceSection, R.id.pyramidSignificance, poi.culturalSignificance)

        // Load images if available
        loadImages(dialogView, poi.images)
    }

    private fun setSectionTextOrHide(dialogView: View, sectionId: Int, textViewId: Int, text: String?) {
        val section = dialogView.findViewById<View>(sectionId)
        val textView = dialogView.findViewById<TextView>(textViewId)

        if (text.isNullOrBlank()) {
            section?.visibility = View.GONE
        } else {
            section?.visibility = View.VISIBLE
            textView?.text = text
        }
    }

    private fun loadImages(dialogView: View, images: List<String>) {
        val imagesScrollView = dialogView.findViewById<View>(R.id.imagesScrollView)
        val imagesContainer = dialogView.findViewById<LinearLayout>(R.id.imagesContainer)

        if (images.isEmpty()) {
            imagesScrollView?.visibility = View.GONE
            return
        }

        imagesScrollView?.visibility = View.VISIBLE
        imagesContainer?.removeAllViews()

        for ((index, imageName) in images.withIndex()) {
            val imageView = ImageView(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    dpToPx(100), // width - reduced from 200 to 100
                    dpToPx(100)  // height - reduced from 200 to 100
                ).apply {
                    marginEnd = dpToPx(8)
                }
                scaleType = ImageView.ScaleType.CENTER_CROP

                // Get drawable resource ID from name
                val resourceId = context.resources.getIdentifier(
                    imageName,
                    "drawable",
                    context.packageName
                )

                if (resourceId != 0) {
                    setImageResource(resourceId)

                    // Make image clickable to open fullscreen viewer
                    isClickable = true
                    isFocusable = true
                    setOnClickListener {
                        showImageViewer(images, index)
                    }
                }
            }

            imagesContainer?.addView(imageView)
        }
    }

    private fun dpToPx(dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }

    private fun setupClickListeners(dialogView: View, poi: POILocation) {
        // Close button
        dialogView.findViewById<ImageButton>(R.id.closeButton)?.setOnClickListener {
            dismiss()
        }
    }

    private fun showImageViewer(images: List<String>, initialIndex: Int) {
        val imageDialog = Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)

            val viewerView = LayoutInflater.from(context).inflate(R.layout.dialog_image_viewer, null)
            setContentView(viewerView)

            var currentIndex = initialIndex
            val imageView = viewerView.findViewById<ImageView>(R.id.fullscreenImageView)
            val pageIndicator = viewerView.findViewById<TextView>(R.id.pageIndicator)
            val previousButton = viewerView.findViewById<ImageButton>(R.id.previousButton)
            val nextButton = viewerView.findViewById<ImageButton>(R.id.nextButton)
            val closeButton = viewerView.findViewById<ImageButton>(R.id.closeButton)
            val navigationContainer = viewerView.findViewById<View>(R.id.navigationContainer)

            // Setup zoom functionality
            setupZoomableImageView(imageView)

            // Function to update the displayed image
            fun updateImage() {
                val imageName = images[currentIndex]
                val resourceId = context.resources.getIdentifier(
                    imageName,
                    "drawable",
                    context.packageName
                )

                if (resourceId != 0) {
                    imageView?.setImageResource(resourceId)
                    // Reset zoom when changing images
                    resetImageMatrix(imageView)
                }

                // Update page indicator
                pageIndicator?.text = "${currentIndex + 1} / ${images.size}"

                // Enable/disable navigation buttons
                previousButton?.isEnabled = currentIndex > 0
                previousButton?.alpha = if (currentIndex > 0) 1.0f else 0.3f

                nextButton?.isEnabled = currentIndex < images.size - 1
                nextButton?.alpha = if (currentIndex < images.size - 1) 1.0f else 0.3f
            }

            // Hide navigation if only one image
            if (images.size == 1) {
                navigationContainer?.visibility = View.GONE
            } else {
                navigationContainer?.visibility = View.VISIBLE

                // Previous button
                previousButton?.setOnClickListener {
                    if (currentIndex > 0) {
                        currentIndex--
                        updateImage()
                    }
                }

                // Next button
                nextButton?.setOnClickListener {
                    if (currentIndex < images.size - 1) {
                        currentIndex++
                        updateImage()
                    }
                }
            }

            // Close button
            closeButton?.setOnClickListener {
                dismiss()
            }

            // Initialize with first image
            updateImage()

            show()
        }
    }

    private fun setupZoomableImageView(imageView: ImageView?) {
        imageView?.let { view ->
            view.scaleType = ImageView.ScaleType.MATRIX

            val matrix = Matrix()
            var scale = 1f
            var lastScale = 1f
            val minScale = 1f
            val maxScale = 5f

            val last = PointF()
            val start = PointF()

            val scaleDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    scale *= detector.scaleFactor
                    scale = max(minScale, min(scale, maxScale))

                    matrix.setScale(scale, scale)

                    // Center the image
                    val viewWidth = view.width.toFloat()
                    val viewHeight = view.height.toFloat()
                    val drawable = view.drawable
                    if (drawable != null) {
                        val drawableWidth = drawable.intrinsicWidth * scale
                        val drawableHeight = drawable.intrinsicHeight * scale

                        var dx = (viewWidth - drawableWidth) / 2
                        var dy = (viewHeight - drawableHeight) / 2

                        // Allow panning only if image is larger than view
                        if (drawableWidth > viewWidth) {
                            dx = max(min(dx, 0f), viewWidth - drawableWidth)
                        }
                        if (drawableHeight > viewHeight) {
                            dy = max(min(dy, 0f), viewHeight - drawableHeight)
                        }

                        matrix.postTranslate(dx, dy)
                    }

                    view.imageMatrix = matrix
                    lastScale = scale
                    return true
                }
            })

            val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onScroll(
                    e1: MotionEvent?,
                    e2: MotionEvent,
                    distanceX: Float,
                    distanceY: Float
                ): Boolean {
                    if (scale > minScale) {
                        matrix.postTranslate(-distanceX, -distanceY)
                        view.imageMatrix = matrix
                        return true
                    }
                    return false
                }

                override fun onDoubleTap(e: MotionEvent): Boolean {
                    if (scale > minScale) {
                        // Reset zoom
                        scale = minScale
                        matrix.reset()
                        matrix.setScale(scale, scale)
                        view.imageMatrix = matrix
                    } else {
                        // Zoom to 2x on double tap
                        scale = 2f
                        matrix.setScale(scale, scale)

                        // Center on tap point
                        val viewWidth = view.width.toFloat()
                        val viewHeight = view.height.toFloat()
                        val drawable = view.drawable
                        if (drawable != null) {
                            val drawableWidth = drawable.intrinsicWidth * scale
                            val drawableHeight = drawable.intrinsicHeight * scale

                            var dx = (viewWidth - drawableWidth) / 2
                            var dy = (viewHeight - drawableHeight) / 2

                            matrix.postTranslate(dx, dy)
                        }

                        view.imageMatrix = matrix
                    }
                    return true
                }
            })

            view.setOnTouchListener { _, event ->
                scaleDetector.onTouchEvent(event)
                gestureDetector.onTouchEvent(event)
                true
            }
        }
    }

    private fun resetImageMatrix(imageView: ImageView?) {
        imageView?.let {
            val matrix = Matrix()
            it.scaleType = ImageView.ScaleType.MATRIX
            it.imageMatrix = matrix
        }
    }

    fun dismiss() {
        dialog?.dismiss()
        dialog = null
    }

    fun isShowing(): Boolean {
        return dialog?.isShowing == true
    }
}
