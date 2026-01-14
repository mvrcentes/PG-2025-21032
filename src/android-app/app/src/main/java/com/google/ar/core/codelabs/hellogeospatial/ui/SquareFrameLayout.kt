package com.google.ar.core.codelabs.hellogeospatial.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

/**
 * A simple container that measures itself to be a square (height equals width).
 * Useful to create square cards without ConstraintLayout or external libs.
 */
class SquareFrameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Force height to match the width for a perfect square
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}