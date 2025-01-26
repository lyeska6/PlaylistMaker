package com.example.playlistmaker.domain.settings.impl

import com.example.playlistmaker.domain.settings.SettingsRepository
import com.example.playlistmaker.domain.settings.SettingsInteractor
import com.example.playlistmaker.domain.settings.model.ThemeSettings

class SettingsInteractorImpl(private val repository: SettingsRepository) : SettingsInteractor {

    override fun updateThemeSettings(
        settings: ThemeSettings
    ) {
        repository.updateThemeSettings(settings)
    }

    override fun getThemeSettings(): ThemeSettings {
        return repository.getThemeSettings()
    }

}