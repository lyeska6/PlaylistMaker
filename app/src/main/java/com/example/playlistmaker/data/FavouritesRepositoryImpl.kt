package com.example.playlistmaker.data

import com.example.playlistmaker.data.converters.TrackDbConvertor
import com.example.playlistmaker.data.db.AppDataBase
import com.example.playlistmaker.domain.favourites.FavouritesRepository
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavouritesRepositoryImpl(
    private val dataBase: AppDataBase,
    private val converter: TrackDbConvertor)
    : FavouritesRepository{

    override suspend fun addTrack(track: Track) {
        dataBase.getTrackDao().insertTrack(converter.map(track))
    }

    override suspend fun deleteTrack(track: Track) {
        dataBase.getTrackDao().deleteTrack(converter.map(track))
    }

    override fun getFavourites(): Flow<List<Track>> = flow{
        val trackList = dataBase.getTrackDao().getFavList().map{track -> converter.map(track)}
        emit(trackList)
    }
}