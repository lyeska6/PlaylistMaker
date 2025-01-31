package com.example.playlistmaker.di

import com.example.playlistmaker.domain.player.AudioplayerInteractor
import com.example.playlistmaker.domain.player.GetChosenTrackUseCase
import com.example.playlistmaker.domain.player.impl.AudioplayerInteractorImpl
import com.example.playlistmaker.domain.player.impl.GetChosenTrackUseCaseImpl
import com.example.playlistmaker.domain.search.SearchHistoryInteractor
import com.example.playlistmaker.domain.search.TracksInteractor
import com.example.playlistmaker.domain.search.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.search.impl.TracksInteractorImpl
import com.example.playlistmaker.domain.settings.SettingsInteractor
import com.example.playlistmaker.domain.settings.impl.SettingsInteractorImpl
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.impl.SharingInteractorImpl
import org.koin.dsl.module

val interactorModule = module {

    factory<AudioplayerInteractor> {
        AudioplayerInteractorImpl(get())
    }

    single<GetChosenTrackUseCase> {
        GetChosenTrackUseCaseImpl(get())
    }

    single<TracksInteractor> {
        TracksInteractorImpl(get())
    }

    single<SearchHistoryInteractor> {
        SearchHistoryInteractorImpl(get())
    }

    single<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }

    single<SharingInteractor> {
        SharingInteractorImpl(get(), get())
    }
}