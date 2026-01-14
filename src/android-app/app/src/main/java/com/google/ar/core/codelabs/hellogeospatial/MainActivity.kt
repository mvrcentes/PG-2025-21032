package com.google.ar.core.codelabs.hellogeospatial

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import android.widget.VideoView
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.ar.core.codelabs.hellogeospatial.helpers.HapticFeedback
import com.google.ar.core.examples.java.common.helpers.FullScreenHelper

class MainActivity : AppCompatActivity() {
    
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initializing the background video
        val videoView = findViewById<VideoView>(R.id.videoBackground)
        val videoUri = Uri.parse("android.resource://${packageName}/raw/kmju")

        videoView.setVideoURI(videoUri)
        videoView.setZ(-100f) // Garantiza que esté al fondo

        videoView.setOnPreparedListener { mp ->
            mp.isLooping = true
            mp.setVolume(0f, 0f)
            videoView.start()
        }

        videoView.setOnErrorListener { _, what, extra ->
            Toast.makeText(this, "Error al reproducir video ($what, $extra)", Toast.LENGTH_LONG).show()
            true
        }
        
        // Get reference to the guided tour card (enhanced teal card)
        val guidedTourCard = findViewById<View>(R.id.enhanced_card_teal)
        // Set click listener for guided tour card to launch AR functionality
        guidedTourCard.setOnClickListener {
            HapticFeedback.navigationTap(this)
            val intent = Intent(this, HelloGeoActivity::class.java)
            startActivity(intent)
        }

        // Listener para la tarjeta de tour libre (enhanced pink card)
        val freeTourCard = findViewById<View>(R.id.enhanced_card_pink)
        freeTourCard.setOnClickListener {
            HapticFeedback.navigationTap(this)
            // Aquí puedes agregar la lógica para el tour libre
            // Por ahora, también lanza la misma actividad AR
            val intent = Intent(this, HelloGeoActivity::class.java)
            startActivity(intent)
        }

        // Listener para la tarjeta de instrucciones (enhanced purple card)
        val instructionsCard = findViewById<View>(R.id.enhanced_card_purple)
        instructionsCard.setOnClickListener {
            HapticFeedback.navigationTap(this)
            val intent = Intent(this, InstructionsActivity::class.java)
            startActivity(intent)
        }

        // Listener para la tarjeta de términos y condiciones (enhanced green card)
        val termsCard = findViewById<View>(R.id.enhanced_card_green)
        termsCard.setOnClickListener {
            HapticFeedback.navigationTap(this)
            val intent = Intent(this, TermsActivity::class.java)
            startActivity(intent)
        }
        
    }
    
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            // Make the app fullscreen when it has focus
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
        }
    }
}
