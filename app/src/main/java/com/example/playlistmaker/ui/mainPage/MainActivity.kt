package com.example.playlistmaker.ui.mainPage

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.playlistmaker.ui.madiatecPage.MediaActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.ui.settingsPage.SettingsActivity
import com.example.playlistmaker.ui.searchPage.SearchActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.statusBarColor = ContextCompat.getColor(this, R.color.main_screen_color)

        val butSearch = findViewById<Button>(R.id.butSearch)
        butSearch.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        val butMedia = findViewById<Button>(R.id.butMedia)
        butMedia.setOnClickListener {
            val mediaIntent: Intent = Intent(this, MediaActivity::class.java)
            startActivity(mediaIntent)
        }

        val butSettings = findViewById<Button>(R.id.butSettings)
        butSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}