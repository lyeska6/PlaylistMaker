package com.example.playlistmaker.creator

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import com.example.playlistmaker.SHARED_PREFS
import com.example.playlistmaker.data.settings.impl.SettingsRepositoryImpl
import com.example.playlistmaker.data.search.impl.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.search.impl.TracksRepositoryImpl
import com.example.playlistmaker.data.search.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.player.AudioPlayerInteractor
import com.example.playlistmaker.domain.settings.SettingsRepository
import com.example.playlistmaker.domain.settings.SettingsInteractor
import com.example.playlistmaker.domain.search.SearchHistoryInteractor
import com.example.playlistmaker.domain.search.SearchHistoryRepository
import com.example.playlistmaker.domain.search.TracksInteractor
import com.example.playlistmaker.domain.search.TracksRepository
import com.example.playlistmaker.domain.sharing.ExternalNavigator
import com.example.playlistmaker.domain.sharing.SharingRepository
import com.example.playlistmaker.data.sharing.impl.ExternalNavigatorImpl
import com.example.playlistmaker.data.sharing.impl.SharingRepositoryImpl
import com.example.playlistmaker.domain.player.impl.AudioPlayerInteractorImpl
import com.example.playlistmaker.domain.settings.impl.SettingsInteractorImpl
import com.example.playlistmaker.domain.search.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.search.impl.TracksInteractorImpl
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.impl.SharingInteractorImpl

object Creator {
    private lateinit var application: Application

    fun initApplication(application: Application) {
        Creator.application = application
    }

    fun getSharedPrefs(): SharedPreferences {
        return application.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
    }

    fun getMediaPlayer(): MediaPlayer {
        return MediaPlayer()
    }

    private fun getSearchHistoryRepository(): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl()
    }

    fun provideSearchHistoryInteractor(): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(getSearchHistoryRepository())
    }

    private fun getSettingsRepository(): SettingsRepository {
        return SettingsRepositoryImpl()
    }

    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository())
    }

    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    fun provideAudioPlayerInteractor(): AudioPlayerInteractor {
        return AudioPlayerInteractorImpl(getSearchHistoryRepository())
    }

    private fun getExternalNavigator(): ExternalNavigator {
        return ExternalNavigatorImpl(application)
    }

    private fun getSharingRepository(): SharingRepository {
        return SharingRepositoryImpl(application)
    }

    fun provideSharingInteractor(): SharingInteractor {
        return SharingInteractorImpl(getExternalNavigator(), getSharingRepository())
    }
}