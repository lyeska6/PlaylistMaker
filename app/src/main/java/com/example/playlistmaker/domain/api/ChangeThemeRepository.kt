package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.DarkThemeState

interface ChangeThemeRepository {

    fun changeTheme(darkThemeEnabled: Boolean): DarkThemeState

    fun getTheme(): DarkThemeState
}