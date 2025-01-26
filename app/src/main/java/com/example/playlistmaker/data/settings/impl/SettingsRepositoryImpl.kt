package com.example.playlistmaker.data.settings.impl

import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.THEME_PREFERENCE_KEY
import com.example.playlistmaker.domain.settings.SettingsRepository
import com.example.playlistmaker.domain.settings.model.ThemeSettings

class SettingsRepositoryImpl: SettingsRepository {

    private val sharedPrefs = Creator.getSharedPrefs()

    override fun updateThemeSettings(settings: ThemeSettings) {
        sharedPrefs.edit().putBoolean(THEME_PREFERENCE_KEY, settings.currentState).apply()
    }

    override fun getThemeSettings(): ThemeSettings {
        val response = sharedPrefs.getBoolean(
            THEME_PREFERENCE_KEY,
            false
        )
        return ThemeSettings(response)
    }
}