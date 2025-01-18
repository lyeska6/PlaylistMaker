package com.example.playlistmaker.data

import com.example.playlistmaker.Creator
import com.example.playlistmaker.THEME_PREFERENCE_KEY
import com.example.playlistmaker.domain.api.ChangeThemeRepository
import com.example.playlistmaker.domain.models.DarkThemeState

class ChangeThemeRepositoryImpl: ChangeThemeRepository {

    private val sharedPrefs = Creator.getSharedPrefs()

    override fun changeTheme(darkThemeEnabled: Boolean): DarkThemeState {
        sharedPrefs.edit().putBoolean(THEME_PREFERENCE_KEY, darkThemeEnabled).apply()
        return DarkThemeState(darkThemeEnabled)
    }

    override fun getTheme(): DarkThemeState {
        val response = sharedPrefs.getBoolean(
            THEME_PREFERENCE_KEY,
            false
        )
        return DarkThemeState(response)
    }
}