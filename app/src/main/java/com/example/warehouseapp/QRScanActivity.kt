package com.example.warehouseapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator

class QRScanActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrscan)
        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        Log.d("TAG", "onActivityResult: here2")
        IntentIntegrator(this).initiateScan()

        val logoutButton = findViewById<Button>(R.id.logoutButton)
        logoutButton.setOnClickListener {
            logout()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("TAG", "onActivityResult: here1")
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Scan cancelled", Toast.LENGTH_SHORT).show()
            } else {
                val packageId = result.contents.trim()
                Log.d("QRScanActivity", "Scanned packageId: $packageId")
                openQRDetailsActivity(packageId)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun openQRDetailsActivity(packageId: String) {
        val intent = Intent(this, QRDetailsActivity::class.java)
        intent.putExtra("packageId", packageId)
        Log.d("QRScanActivity", "Opening QRDetailsActivity with packageId: $packageId")
        startActivity(intent)
        finish()
    }

    private fun logout() {

        // Clear SharedPreferences
        val editor = sharedPreferences.edit()
        editor.clear()  // This removes all entries from SharedPreferences
        editor.apply()

        // Notify user
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()

        // Navigate to LoginActivity
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
