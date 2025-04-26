package com.example.playlistmaker.ui.madiatec.view_model

import com.example.playlistmaker.domain.playlists.model.Playlist

sealed class PlaylistsState {
    data object StateEmpty: PlaylistsState()
    data class StateData(
        val playlists: List<Playlist>
    ): PlaylistsState()
}