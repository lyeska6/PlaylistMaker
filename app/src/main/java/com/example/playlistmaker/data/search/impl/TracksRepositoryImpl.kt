package com.example.playlistmaker.data.search.impl

import com.example.playlistmaker.data.search.NetworkClient
import com.example.playlistmaker.data.search.dto.TracksSearchRequest
import com.example.playlistmaker.data.search.dto.TracksSearchResponse
import com.example.playlistmaker.domain.search.TracksRepository
import com.example.playlistmaker.domain.search.model.SearchedTracksResponse
import com.example.playlistmaker.domain.search.model.Track

class TracksRepositoryImpl(
    private val networkClient: NetworkClient
) : TracksRepository {

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