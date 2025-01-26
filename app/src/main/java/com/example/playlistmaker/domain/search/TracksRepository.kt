package com.example.playlistmaker.domain.search

import com.example.playlistmaker.domain.search.model.SearchedTracksResponse

interface TracksRepository {
    fun searchTracks(searchRequest: String): SearchedTracksResponse
}