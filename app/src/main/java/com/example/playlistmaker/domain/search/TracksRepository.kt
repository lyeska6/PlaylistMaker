package com.example.playlistmaker.domain.search

import com.example.playlistmaker.domain.search.model.SearchedTracksResponse
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    fun searchTracks(searchRequest: String): Flow<SearchedTracksResponse>
}