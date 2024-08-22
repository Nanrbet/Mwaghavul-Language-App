package com.example.mwaghavullexicon

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity

class CustomSplashScreen : AppCompatActivity() {

    private lateinit var rootView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        rootView = findViewById(R.id.root_view)
        startAnimation()

        // Start MainActivity after the splash screen
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 5000)  // Adjust delay as needed
    }

    private fun startAnimation() {
        val slideInAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_right)
        rootView.startAnimation(slideInAnimation)
    }
}