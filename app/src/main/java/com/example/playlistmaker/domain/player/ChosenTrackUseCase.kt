package com.example.playlistmaker.domain.player

import com.example.playlistmaker.domain.search.model.Track

interface ChosenTrackUseCase {
    fun getTrack(): Track
    fun setTrack(track: Track)
}