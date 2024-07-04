package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity:  AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val arrowBackButton: ImageView = findViewById<ImageView>(R.id.arrowBack)
        arrowBackButton.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}