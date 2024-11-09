package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.switchmaterial.SwitchMaterial

const val SHARED_PREFS = "shared_prefs"
const val THEME_PREFERENCE_KEY = "dark_theme_is"
const val KEY_SEARCH_HISTORY_LIST = "search_history_list"

class App : Application() {

    private var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        val sharedPrefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)
        darkTheme = sharedPrefs.getBoolean(THEME_PREFERENCE_KEY, false)
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}