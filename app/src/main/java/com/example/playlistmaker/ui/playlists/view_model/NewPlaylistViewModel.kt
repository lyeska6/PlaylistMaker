package com.example.playlistmaker.ui.playlists.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.playlists.PlaylistsInteractor
import com.example.playlistmaker.domain.playlists.model.Playlist
import kotlinx.coroutines.launch

open class NewPlaylistViewModel(
    private val interactor: PlaylistsInteractor
): ViewModel() {

    companion object {
        const val DIR_NAME = "PlaylistsCoversDir"
    }

    fun createPlaylist(name: String, description: String?, coverPath: String) {
        val playlist = Playlist(0,
            name,
            if (!description.isNullOrEmpty()) description else "",
            coverPath,
            emptyList<String>(),
            0
        )
        viewModelScope.launch {
            interactor.addPlaylist(playlist)
        }
    }
}