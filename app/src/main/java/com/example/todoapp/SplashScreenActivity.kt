package com.example.todoapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.airbnb.lottie.LottieAnimationView
import com.example.todoapp.databinding.ActivitySplashScreenBinding

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.hide()

        val animation = findViewById<LottieAnimationView>(R.id.lottie_splash)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val textView = binding.tvToDoApp
        textView.alpha = 0f

        textView.animate().setDuration(3000).alpha(1f)
        animation.animate().setStartDelay(500).setDuration(3000).alpha(1f).withEndAction {
            val moveToHome = Intent(this, MainActivity::class.java)
            startActivity(moveToHome)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }
}