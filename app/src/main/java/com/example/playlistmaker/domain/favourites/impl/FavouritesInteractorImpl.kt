package com.example.playlistmaker.domain.favourites.impl

import com.example.playlistmaker.domain.favourites.FavouritesInteractor
import com.example.playlistmaker.domain.favourites.FavouritesRepository
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavouritesInteractorImpl(
    private val repository: FavouritesRepository
): FavouritesInteractor {

    override suspend fun addTrack(track: Track) {
        repository.addTrack(track)
    }

    override suspend fun deleteTrack(track: Track) {
        repository.deleteTrack(track)
    }

    override fun getFavourites(): Flow<List<Track>> {
        return repository.getFavourites().map { trackList ->  trackList.reversed()}
    }

}