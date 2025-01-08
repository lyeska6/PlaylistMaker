package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.SearchedTracksResponse

interface TracksRepository {
    fun searchTracks(searchRequest: String): SearchedTracksResponse
}