package com.example.playlistmaker.ui.playlists.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.playlists.PlaylistsInteractor
import com.example.playlistmaker.domain.playlists.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor
): ViewModel() {

    private val playlistLiveData = MutableLiveData<PlaylistState>()

    fun getPlaylistLiveData(): LiveData<PlaylistState> = playlistLiveData

    fun getPlaylist(id: Long) {
        viewModelScope.launch {
            playlistsInteractor.getPlaylistById(id).collect{ playlist ->
                postPlaylist(playlist)
            }
        }
    }

    fun deleteTrack(track: Track) {
        val playlist = when (val playlistState = playlistLiveData.value) {
            is PlaylistState.NoTracks -> playlistState.playlist
            is PlaylistState.Tracks -> playlistState.playlist
            else -> null
        }
        viewModelScope.launch {
            if (playlist != null) {
                playlistsInteractor.deleteTrackFromPlaylist(track, playlist).collect { playlist ->
                    postPlaylist(playlist)
                }
            }
        }
    }

    fun onTrackClick(track: Track) {
        playlistsInteractor.setChosenTrack(track)
    }

    fun deletePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistsInteractor.deletePlaylist(playlist)
        }
    }

    fun sharePlaylist(message: String) {
        playlistsInteractor.sharePlaylist(message)
    }

    private suspend fun postPlaylist(playlist: Playlist) {
        if (playlist.size == 0) {
            playlistLiveData.postValue(PlaylistState.NoTracks(playlist))
        } else {
            var tracks = emptyList<Track>()
            playlistsInteractor.getListOfTracksById(playlist.tracksIdList).collect{ tracksList ->
                tracks = tracksList
            }
            playlistLiveData.postValue(PlaylistState.Tracks(playlist, tracks))
        }
    }
}