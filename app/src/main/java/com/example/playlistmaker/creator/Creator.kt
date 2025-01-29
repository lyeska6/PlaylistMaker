package com.example.playlistmaker.creator

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import com.example.playlistmaker.SHARED_PREFS
import com.example.playlistmaker.data.player.impl.AudioplayerRepositoryImpl
import com.example.playlistmaker.data.settings.impl.SettingsRepositoryImpl
import com.example.playlistmaker.data.search.impl.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.search.impl.TracksRepositoryImpl
import com.example.playlistmaker.data.search.network.RetrofitNetworkClient
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
import com.example.playlistmaker.domain.player.AudioplayerInteractor
import com.example.playlistmaker.domain.player.AudioplayerRepository
import com.example.playlistmaker.domain.player.GetChosenTrackUseCase
import com.example.playlistmaker.domain.player.impl.AudioplayerInteractorImpl
import com.example.playlistmaker.domain.player.impl.GetChosenTrackUseCaseImpl
import com.example.playlistmaker.domain.settings.impl.SettingsInteractorImpl
import com.example.playlistmaker.domain.search.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.search.impl.TracksInteractorImpl
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.impl.SharingInteractorImpl
import com.google.gson.Gson

object Creator {
    private lateinit var application: Application

    fun initApplication(application: Application) {
        Creator.application = application
    }

    private fun getSharedPrefs(): SharedPreferences {
        return application.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
    }

    private fun getGson(): Gson {
        return Gson()
    }

    private fun getMediaPlayer(): MediaPlayer {
        return MediaPlayer()
    }

    private fun getAudioplayerRepository(): AudioplayerRepository{
        return AudioplayerRepositoryImpl(getMediaPlayer())
    }

    fun provideAudioplauerInteractor(): AudioplayerInteractor{
        return AudioplayerInteractorImpl(getAudioplayerRepository())
    }

    private fun getSearchHistoryRepository(): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(getSharedPrefs(), getGson())
    }

    fun provideSearchHistoryInteractor(): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(getSearchHistoryRepository())
    }

    private fun getSettingsRepository(): SettingsRepository {
        return SettingsRepositoryImpl(getSharedPrefs())
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

    fun provideGetChosenTrackUseCase(): GetChosenTrackUseCase {
        return GetChosenTrackUseCaseImpl(getSearchHistoryRepository())
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