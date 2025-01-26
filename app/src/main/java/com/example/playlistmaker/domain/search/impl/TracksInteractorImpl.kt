package com.example.playlistmaker.domain.search.impl

import com.example.playlistmaker.domain.search.TracksInteractor
import com.example.playlistmaker.domain.search.TracksRepository
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
                foundConsumer.consume(ArrayList(response.trackList))
            } else if (response.resultCode == 200) {
                nothingFoundConsumer.consume(ArrayList(response.trackList))
            } else {
                networkErrorConsumer.consume(ArrayList(response.trackList))
            }
        }
    }
}