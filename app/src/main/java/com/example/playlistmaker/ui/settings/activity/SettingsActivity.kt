package com.example.playlistmaker.ui.settings.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel


class SettingsActivity : AppCompatActivity() {

    private lateinit var viewModel : SettingsViewModel
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.screen_color)

        viewModel = ViewModelProvider(this, SettingsViewModel.getViewModelFactory())[SettingsViewModel::class.java]

        viewModel.getDarkThemeLiveData().observe(this){ state ->
            setTheme(state)
        }

        binding.themeSwitcher.setOnCheckedChangeListener { _, checked ->
            viewModel.setThemeState(checked)
        }

        binding.shareApp.setOnClickListener {
            viewModel.shareApp()
        }

        binding.supportBut.setOnClickListener {
            viewModel.openSupport()
        }

        binding.agreementBut.setOnClickListener {
            viewModel.openTerms()
        }

        binding.buttonBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setTheme(state: Boolean) {
        binding.themeSwitcher.setChecked(state)
        (applicationContext as App).switchTheme(state)
    }
}