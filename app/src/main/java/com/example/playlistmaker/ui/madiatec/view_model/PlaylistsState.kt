package com.example.playlistmaker.ui.madiatec.view_model

sealed class PlaylistsState {
    data object StateEmpty: PlaylistsState()
}