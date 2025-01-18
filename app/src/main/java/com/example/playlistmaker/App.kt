package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

const val SHARED_PREFS = "shared_prefs"
const val THEME_PREFERENCE_KEY = "dark_theme_is"
const val KEY_SEARCH_HISTORY_LIST = "search_history_list"
const val KEY_CHOSEN_TRACK = "chosen_track"

class App : Application() {


    override fun onCreate() {
        super.onCreate()

        Creator.initApplication(this)

        val darkThemeInteractor = Creator.provideDarkThemeInteractor()

        switchTheme(darkThemeInteractor.getTheme().currentState)
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