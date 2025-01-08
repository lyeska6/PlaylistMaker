package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.DarkThemeState

interface DarkThemeInteractor {

    fun changeTheme(darkThemeEnabled: Boolean, consumer: ChangeThemeConsumer)

    interface ChangeThemeConsumer {
        fun consume(darkThemeState: DarkThemeState)
    }

    fun getTheme(): DarkThemeState
}