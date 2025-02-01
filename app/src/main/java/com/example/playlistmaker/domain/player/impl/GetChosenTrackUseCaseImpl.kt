package com.example.playlistmaker.domain.player.impl

import com.example.playlistmaker.domain.player.GetChosenTrackUseCase
import com.example.playlistmaker.domain.search.SearchHistoryRepository
import com.example.playlistmaker.domain.search.model.Track

class GetChosenTrackUseCaseImpl(
    private val repository: SearchHistoryRepository
): GetChosenTrackUseCase {
    override fun getTrack(): Track {
        return repository.getChosenTrack()
    }
}