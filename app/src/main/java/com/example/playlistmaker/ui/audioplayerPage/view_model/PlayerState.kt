package com.example.playlistmaker.ui.audioplayerPage.view_model

sealed class PlayerState {
    data object StateDefault: PlayerState()
    data object StatePrepared: PlayerState()
    data object StatePlaying: PlayerState()
    data object StatePaused: PlayerState()
}