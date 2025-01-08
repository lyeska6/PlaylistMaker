package com.example.playlistmaker.data

import com.example.playlistmaker.data.dto.TracksSearchRequest
import com.example.playlistmaker.data.dto.TracksSearchResponse
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.SearchedTracksResponse
import com.example.playlistmaker.domain.models.Track

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTracks(searchRequest: String): SearchedTracksResponse {
        val response = networkClient.doRequest(TracksSearchRequest(searchRequest))
        val searchedTracksResponse = SearchedTracksResponse(response.resultCode)
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
                    it.previewUrl
                )
            }
        }
        return searchedTracksResponse
    }
}