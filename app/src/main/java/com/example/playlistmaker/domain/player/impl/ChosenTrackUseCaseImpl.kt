package com.example.playlistmaker.domain.player.impl

import com.example.playlistmaker.domain.player.ChosenTrackUseCase
import com.example.playlistmaker.domain.search.SearchHistoryRepository
import com.example.playlistmaker.domain.search.model.Track

class ChosenTrackUseCaseImpl(
    private val repository: SearchHistoryRepository
): ChosenTrackUseCase {
    override fun getTrack(): Track {
        return repository.getChosenTrack()
    }

    override fun setTrack(track: Track) {
        repository.setChosenTrack(track)
    }
}