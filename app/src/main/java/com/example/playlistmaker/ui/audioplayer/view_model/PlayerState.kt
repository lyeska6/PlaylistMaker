package com.example.playlistmaker.ui.audioplayer.view_model

sealed class PlayerState (
    val timing: String
){
    class StateDefault: PlayerState("00:00")
    class StatePrepared: PlayerState("00:00")
    class StatePlaying(timing: String): PlayerState(timing)
    class StatePaused(timing: String): PlayerState(timing)
}