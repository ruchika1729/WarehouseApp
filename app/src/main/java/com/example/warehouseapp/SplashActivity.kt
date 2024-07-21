package com.example.warehouseapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private val SPLASH_SCREEN_DELAY: Long = 2000 // Delay in milliseconds
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)

        Handler().postDelayed({
            val email = sharedPreferences.getString("user_email", null)
            val intent = if (email != null) {
                Intent(this, QRScanActivity::class.java)
            } else {
                Intent(this, LoginActivity::class.java)
            }
            startActivity(intent)
            finish()
        }, SPLASH_SCREEN_DELAY)
    }
}
