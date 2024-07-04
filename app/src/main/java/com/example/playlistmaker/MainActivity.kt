package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val butSearch = findViewById<Button>(R.id.butSearch)
        butSearch.setOnClickListener{
            startActivity(Intent(this, SearchActivity::class.java))
        }

        val butMedia = findViewById<Button>(R.id.butMedia)
        butMedia.setOnClickListener{
            val mediaIntent: Intent = Intent(this, MediaActivity::class.java)
            startActivity(mediaIntent)
        }

        val butSettings = findViewById<Button>(R.id.butSettings)
        butSettings.setOnClickListener{
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}