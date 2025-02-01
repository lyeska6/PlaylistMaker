package com.example.playlistmaker.ui.search.view_model

import com.example.playlistmaker.domain.search.model.Track

sealed class SearchScreenState {
    data object Default: SearchScreenState()
    data class SearchHistoryView(
        val tracks: ArrayList<Track>
    ): SearchScreenState()
    data class SearchTracksView(
        val isLoading: Boolean,
        val nothingFound: Boolean,
        val networkError: Boolean,
        val tracks: ArrayList<Track>
    ): SearchScreenState()
}