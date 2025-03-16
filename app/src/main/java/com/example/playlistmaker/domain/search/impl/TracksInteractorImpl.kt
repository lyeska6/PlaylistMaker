package com.example.playlistmaker.domain.search.impl

import com.example.playlistmaker.domain.search.TracksInteractor
import com.example.playlistmaker.domain.search.TracksRepository
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.Executors

class TracksInteractorImpl(
    private val repository: TracksRepository
): TracksInteractor {

    override fun searchTracks(
        expression: String
    ): Flow<Pair<ArrayList<Track>?, Int?>> {
        return repository.searchTracks(expression).map { response ->
            if (response.resultCode == 200 && response.trackList.isNotEmpty()) {
                Pair(ArrayList(response.trackList), null)
            } else if (response.resultCode == 200){
                Pair(null, -1)
            } else {
                Pair(null, response.resultCode)
            }
        }
    }
}