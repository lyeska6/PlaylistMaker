package com.example.playlistmaker.ui.settings.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.settings.SettingsInteractor
import com.example.playlistmaker.domain.settings.model.ThemeSettings
import com.example.playlistmaker.domain.sharing.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor
): ViewModel() {

    private val darkThemeLiveData = MutableLiveData(settingsInteractor.getThemeSettings(false).currentState)

    fun getDarkThemeLiveData(): LiveData<Boolean> = darkThemeLiveData

    fun setThemeState(state: Boolean) {
        settingsInteractor.updateThemeSettings(ThemeSettings(state))
        darkThemeLiveData.postValue(state)
    }

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    fun openTerms() {
        sharingInteractor.openTerms()
    }

    fun openSupport() {
        sharingInteractor.openSupport()
    }
}