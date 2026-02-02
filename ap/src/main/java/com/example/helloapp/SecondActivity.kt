package com.example.helloapp

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class SecondActivity : AppCompatActivity() {

    // 📸 Gallery
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                Toast.makeText(this, "Image selected 🖼️", Toast.LENGTH_SHORT).show()
            }
        }

    // 📷 Camera
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "Photo captured 📸", Toast.LENGTH_SHORT).show()
            }
        }

    // 📄 PDF
    private val pdfLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                Toast.makeText(this, "PDF selected 📄", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)
        val email = intent.getStringExtra("EMAIL") ?: ""
        tvWelcome.text = "Welcome ${email.substringBefore("@")} 👋"

        // 📸 Gallery
        findViewById<Button>(R.id.btnGallery).setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        // 📷 Camera
        findViewById<Button>(R.id.btnCamera).setOnClickListener {

            if (!hasCameraPermission()) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.CAMERA),
                    2001
                )
                return@setOnClickListener
            }

            cameraLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        }

        // 📄 PDF
        findViewById<Button>(R.id.btnPdf).setOnClickListener {
            pdfLauncher.launch("application/pdf")
        }

        // ⏰ Reminder
        findViewById<Button>(R.id.btnReminder).setOnClickListener {
            startActivity(Intent(this, ReminderActivity::class.java))
        }

        // ⬅ Back
        findViewById<TextView>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }

    private fun hasCameraPermission(): Boolean {
        return checkSelfPermission(android.Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 2001 &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            cameraLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        }
    }
}
