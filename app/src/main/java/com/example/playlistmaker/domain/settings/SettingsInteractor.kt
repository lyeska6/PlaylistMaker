package com.example.playlistmaker.domain.settings

import com.example.playlistmaker.domain.settings.model.ThemeSettings

interface SettingsInteractor {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSettings(settings: ThemeSettings)
}