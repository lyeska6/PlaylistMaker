package com.example.playlistmaker.ui.audioplayer.view_model

sealed class PlayerState {
    data object StateDefault: PlayerState()
    data object StatePrepared: PlayerState()
    data class StatePlaying(
        val timing: Int
    ): PlayerState()
    data object StatePaused: PlayerState()
}