package com.example.playlistmaker.data.settings.impl

import android.content.SharedPreferences
import com.example.playlistmaker.THEME_PREFERENCE_KEY
import com.example.playlistmaker.domain.settings.SettingsRepository
import com.example.playlistmaker.domain.settings.model.ThemeSettings

class SettingsRepositoryImpl(
    private val sharedPrefs: SharedPreferences
): SettingsRepository {

    override fun updateThemeSettings(settings: ThemeSettings) {
        sharedPrefs.edit().putBoolean(THEME_PREFERENCE_KEY, settings.currentState).apply()
    }

    override fun getThemeSettings(elseTheme: Boolean): ThemeSettings {
        return if (sharedPrefs.contains(THEME_PREFERENCE_KEY)) {
            ThemeSettings(sharedPrefs.getBoolean(
                THEME_PREFERENCE_KEY,
                false)
            )
        } else {
            sharedPrefs.edit()
                .putBoolean(THEME_PREFERENCE_KEY, elseTheme)
                .apply()
            ThemeSettings(elseTheme)
        }
    }
}