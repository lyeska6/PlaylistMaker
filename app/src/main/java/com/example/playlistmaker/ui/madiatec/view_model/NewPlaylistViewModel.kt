package com.example.playlistmaker.ui.madiatec.view_model

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.playlists.PlaylistsInteractor
import com.example.playlistmaker.domain.playlists.model.Playlist
import kotlinx.coroutines.launch

class NewPlaylistViewModel(
    private val interactor: PlaylistsInteractor
): ViewModel() {

    companion object {
        const val DIR_NAME = "PlaylistsCoversDir"
    }

    fun createPlaylist(name: String, description: String?, isCoverLoaded: Uri?) {
        val playlist = Playlist(0,
            name,
            if (!description.isNullOrEmpty()) description else "",
            DIR_NAME,
            emptyList<String>(),
            0
        )
        viewModelScope.launch {
            interactor.addPlaylist(playlist)
        }
    }
}