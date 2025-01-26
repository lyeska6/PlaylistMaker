package com.example.playlistmaker.ui.search.view_model

sealed class SearchScreenState {
    data object Default: SearchScreenState()
    data object SearchHistoryView : SearchScreenState()
    data class SearchTracksView(
        val isLoading: Boolean,
        val nothingFound: Boolean,
        val networkError: Boolean
    ): SearchScreenState()
}