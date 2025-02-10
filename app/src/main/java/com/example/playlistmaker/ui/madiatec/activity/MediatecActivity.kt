package com.example.playlistmaker.ui.madiatec.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityMediaBinding
import com.google.android.material.tabs.TabLayoutMediator

class MediatecActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMediaBinding
    private lateinit var tabsMediator: TabLayoutMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.screen_color)

        binding.viewPager.adapter = MediatecTabsAdapter(supportFragmentManager, lifecycle)

        tabsMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.favourite_tracks)
                1 -> tab.text = getString(R.string.playlists)
            }
        }
        tabsMediator.attach()

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tabsMediator.detach()
    }
}