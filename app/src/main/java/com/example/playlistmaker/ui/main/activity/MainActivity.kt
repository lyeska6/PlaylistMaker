package com.example.playlistmaker.ui.main.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.playlistmaker.ui.madiatecPage.activity.MediatecActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityMainBinding
import com.example.playlistmaker.ui.settings.activity.SettingsActivity
import com.example.playlistmaker.ui.search.activity.SearchActivity

class MainActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.main_screen_color)

        binding.butSearch.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        binding.butMedia.setOnClickListener {
            startActivity(Intent(this, MediatecActivity::class.java))
        }

        binding.butSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}