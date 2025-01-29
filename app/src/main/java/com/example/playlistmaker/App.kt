package com.example.playlistmaker

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.creator.Creator

const val SHARED_PREFS = "shared_prefs"
const val THEME_PREFERENCE_KEY = "dark_theme_is"
const val KEY_SEARCH_HISTORY_LIST = "search_history_list"
const val KEY_CHOSEN_TRACK = "chosen_track"

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        Creator.initApplication(this)

        val settingsInteractor = Creator.provideSettingsInteractor()

        switchTheme(settingsInteractor.getThemeSettings(isDarkThemeOn()).currentState)
    }

    fun isDarkThemeOn(): Boolean {
        return resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}