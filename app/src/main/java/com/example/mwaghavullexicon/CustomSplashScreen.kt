package com.example.mwaghavullexicon

import android.animation.AnimatorInflater
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class CustomSplashScreen : AppCompatActivity() {

    private lateinit var logo: ImageView
    private lateinit var rootView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        logo = findViewById(R.id.logo)
        rootView = findViewById(R.id.root_view)
        startAnimation()

        // Start MainActivity after the splash screen
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 3000)  // Adjust delay as needed
    }

    private fun startAnimation() {
        val slideInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        rootView.startAnimation(slideInAnimation)

        val animator = AnimatorInflater.loadAnimator(this, R.animator.logo_animator)
        animator.setTarget(logo)
        Handler(Looper.getMainLooper()).postDelayed({
            animator.start()
        }, 500)
    }
}