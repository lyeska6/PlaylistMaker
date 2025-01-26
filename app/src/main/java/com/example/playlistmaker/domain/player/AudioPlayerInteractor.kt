package com.example.playlistmaker.domain.player

import com.example.playlistmaker.domain.search.model.Track

interface AudioPlayerInteractor {
    fun getTrack(): Track
}