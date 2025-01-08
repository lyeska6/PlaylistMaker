package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.DarkThemeInteractor
import com.example.playlistmaker.domain.api.SharedPrefsRepository
import com.example.playlistmaker.domain.models.DarkThemeState

class DarkThemeInteractorImpl(private val repository: SharedPrefsRepository) : DarkThemeInteractor {

    override fun changeTheme(
        darkThemeEnabled: Boolean,
        consumer: DarkThemeInteractor.ChangeThemeConsumer
    ) {
        consumer.consume(repository.changeTheme(darkThemeEnabled))
    }

    override fun getTheme(): DarkThemeState {
        return repository.getTheme()
    }

}