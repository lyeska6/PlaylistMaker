package com.example.playlistmaker.domain.search

import com.example.playlistmaker.domain.search.model.Track

interface TracksInteractor {

    fun searchTracks(
        expression: String,
        foundConsumer: TracksConsumer,
        nothingFoundConsumer: TracksConsumer,
        networkErrorConsumer: TracksConsumer
    )

    interface TracksConsumer {
        fun consume(tracks: ArrayList<Track>)
    }
}