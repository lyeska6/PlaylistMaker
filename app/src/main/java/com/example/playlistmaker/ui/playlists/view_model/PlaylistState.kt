package com.example.playlistmaker.ui.playlists.view_model

import com.example.playlistmaker.domain.playlists.model.Playlist
import com.example.playlistmaker.domain.search.model.Track

sealed class PlaylistState{
    data object Empty: PlaylistState()
    data class NoTracks(
        val playlist: Playlist
    ): PlaylistState()
    data class Tracks(
        val playlist: Playlist,
        val tracks: List<Track>
    ): PlaylistState()
}