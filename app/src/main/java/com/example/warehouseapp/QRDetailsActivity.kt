package com.example.warehouseapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class QRDetailsActivity : AppCompatActivity() {

    private lateinit var packageIdEditText: EditText
    private lateinit var totalItemsEditText: EditText
    private lateinit var costEditText: EditText
    private lateinit var expiryDateEditText: EditText
    private lateinit var saveButton: Button

    private lateinit var firestore: FirebaseFirestore
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrdetails)
        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)

        firestore = FirebaseFirestore.getInstance()

        packageIdEditText = findViewById(R.id.packageIdEditText)
        totalItemsEditText = findViewById(R.id.totalItemsEditText)
        costEditText = findViewById(R.id.costEditText)
        expiryDateEditText = findViewById(R.id.expiryDateEditText)
        saveButton = findViewById(R.id.saveButton)

        val packageId = intent.getStringExtra("packageId")?.trim()

        Log.d("QRDetailsActivity", "Received packageId: $packageId")

        if (!packageId.isNullOrBlank()) {

            val safePackageId = packageId.replace("/", "_")
            fetchPackageDetails(safePackageId)
        } else {
            Toast.makeText(this, "Invalid package ID", Toast.LENGTH_SHORT).show()
        }

        saveButton.setOnClickListener {

            val updatedPackageId = packageIdEditText.text.toString().trim().replace("/", "_")
            val totalItems = totalItemsEditText.text.toString().trim()
            val cost = costEditText.text.toString().trim()
            val expiryDate = expiryDateEditText.text.toString().trim()

            if (updatedPackageId.isNotEmpty()) {
                savePackageDetails(updatedPackageId, totalItems, cost, expiryDate)
            } else {
                Toast.makeText(this, "Please enter a package ID", Toast.LENGTH_SHORT).show()
            }
        }
        val logoutButton = findViewById<Button>(R.id.logoutButton1)
        logoutButton.setOnClickListener {
            logout()
        }
    }

    private fun fetchPackageDetails(packageId: String) {

        firestore.collection("warehouse").document(packageId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Document found, populate UI fields
                    packageIdEditText.setText(document.id)
                    totalItemsEditText.setText(document.getString("totalItems") ?: "")
                    costEditText.setText(document.getString("cost") ?: "")
                    expiryDateEditText.setText(document.getString("expiryDate") ?: "")
                } else {
                    Toast.makeText(this, "Package details not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching package details: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun savePackageDetails(packageId: String, totalItems: String, cost: String, expiryDate: String) {
        val packageDetails = hashMapOf(
            "totalItems" to totalItems,
            "cost" to cost,
            "expiryDate" to expiryDate
        )

        firestore.collection("warehouse").document(packageId)
            .set(packageDetails)
            .addOnSuccessListener {
                Toast.makeText(this, "Package details saved successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving package details: ${e.message}", Toast.LENGTH_SHORT).show()
            }
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
