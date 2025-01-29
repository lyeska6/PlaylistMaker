package com.example.playlistmaker.domain.player

import com.example.playlistmaker.domain.search.model.Track

interface GetChosenTrackUseCase {
    fun getTrack(): Track
}