package com.example.playlistmaker.domain.player.impl

import com.example.playlistmaker.domain.player.AudioPlayerInteractor
import com.example.playlistmaker.domain.search.SearchHistoryRepository
import com.example.playlistmaker.domain.search.model.Track

class AudioPlayerInteractorImpl(
    private val repository: SearchHistoryRepository
) : AudioPlayerInteractor {

    override fun getTrack(): Track {
        return repository.getChosenTrack()
    }
}