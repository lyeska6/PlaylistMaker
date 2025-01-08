package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(
        expression: String,
        foundConsumer: TracksInteractor.TracksConsumer,
        nothingFoundConsumer: TracksInteractor.TracksConsumer,
        networkErrorConsumer: TracksInteractor.TracksConsumer
    ) {
        executor.execute {
            val response = repository.searchTracks(expression)
            if (response.resultCode == 200 && response.trackList.isNotEmpty()) {
                foundConsumer.consume(response.trackList)
            } else if (response.resultCode == 200) {
                nothingFoundConsumer.consume(response.trackList)
            } else {
                networkErrorConsumer.consume(response.trackList)
            }
        }

    }

}