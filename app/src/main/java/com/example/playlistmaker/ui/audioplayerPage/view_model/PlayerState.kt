package com.example.playlistmaker.ui.audioplayerPage.view_model

sealed class PlayerState {
    data object StateDefault: PlayerState()
    data object StatePrepared: PlayerState()
    data class StatePlaying(
        val timing: Int
    ): PlayerState()
    data object StatePaused: PlayerState()
}