package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface TracksInteractor {

    fun searchTracks(
        expression: String,
        foundConsumer: TracksConsumer,
        nothingFoundConsumer: TracksConsumer,
        networkErrorConsumer: TracksConsumer
    )

    interface TracksConsumer {
        fun consume(tracks: List<Track>)
    }
}