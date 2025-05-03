package com.example.playlistmaker.di

import com.example.playlistmaker.ui.audioplayer.view_model.AudioplayerViewModel
import com.example.playlistmaker.ui.madiatec.view_model.FavouritesViewModel
import com.example.playlistmaker.ui.playlists.view_model.NewPlaylistViewModel
import com.example.playlistmaker.ui.playlists.view_model.PlaylistRedactorViewModel
import com.example.playlistmaker.ui.playlists.view_model.PlaylistViewModel
import com.example.playlistmaker.ui.playlists.view_model.PlaylistsViewModel
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        AudioplayerViewModel(get(), get(), get(), get())
    }

    viewModel {
        SearchViewModel(get(), get())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel {
        FavouritesViewModel(get(), get())
    }

    viewModel {
        PlaylistsViewModel(get())
    }

    viewModel {
        NewPlaylistViewModel(get())
    }

    viewModel {
        PlaylistViewModel(get())
    }

    viewModel {
        PlaylistRedactorViewModel(get())
    }
}