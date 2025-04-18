package com.example.playlistmaker.data.search.impl

import com.example.playlistmaker.data.db.AppDataBase
import com.example.playlistmaker.data.search.NetworkClient
import com.example.playlistmaker.data.search.dto.TracksSearchRequest
import com.example.playlistmaker.data.search.dto.TracksSearchResponse
import com.example.playlistmaker.domain.search.TracksRepository
import com.example.playlistmaker.domain.search.model.SearchedTracksResponse
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val dataBase: AppDataBase
) : TracksRepository {

    override fun searchTracks(searchRequest: String): Flow<SearchedTracksResponse> = flow {
        val response = networkClient.doRequest(TracksSearchRequest(searchRequest))
        val searchedTracksResponse = SearchedTracksResponse(response.resultCode)
        val idList = dataBase.getTrackDao().getFavIds()
        if (response.resultCode == 200) {
            searchedTracksResponse.trackList = (response as TracksSearchResponse).results.map {
                Track(
                    it.trackName,
                    it.artistName,
                    it.trackTimeMillis,
                    it.artworkUrl100,
                    it.trackId,
                    it.collectionName,
                    it.releaseDate,
                    it.primaryGenreName,
                    it.country,
                    it.previewUrl,
                    it.trackId in idList
                )
            }
        }
        emit(searchedTracksResponse)
    }
}