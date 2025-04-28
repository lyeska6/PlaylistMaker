package com.example.playlistmaker.ui.playlists.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.playlists.PlaylistsInteractor
import com.example.playlistmaker.domain.playlists.model.Playlist
import kotlinx.coroutines.launch

class PlaylistRedactorViewModel(
    private val interactor: PlaylistsInteractor
): NewPlaylistViewModel(interactor) {

    private val playlistLiveData = MutableLiveData<PlaylistState>(PlaylistState.Empty)

    fun getPlaylistLiveData(): LiveData<PlaylistState> = playlistLiveData

    fun getPlaylist(id: Long) {
        viewModelScope.launch {
            interactor.getPlaylistById(id).collect{ playlist ->
                playlistLiveData.postValue(PlaylistState.NoTracks(playlist))
            }
        }
    }

    fun updatePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            interactor.updatePlaylist(playlist)
        }
    }
}