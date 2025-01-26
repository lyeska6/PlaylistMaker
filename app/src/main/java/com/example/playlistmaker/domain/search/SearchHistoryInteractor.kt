package com.example.playlistmaker.domain.search

import com.example.playlistmaker.domain.search.model.Track

interface SearchHistoryInteractor {

    fun getSearchHistory(): ArrayList<Track>

    fun addTrack(track: Track)

    fun clearHistory()
}