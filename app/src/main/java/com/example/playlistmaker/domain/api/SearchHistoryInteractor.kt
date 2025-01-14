package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface SearchHistoryInteractor {

    fun getSearchHistory(): ArrayList<Track>

    fun addTrack(track: Track)

    fun clearHistory()

    fun isSearchHistoryNullOrEmpty(): Boolean
}