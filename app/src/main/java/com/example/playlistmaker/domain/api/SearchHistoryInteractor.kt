package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface SearchHistoryInteractor {

    fun setSearchHistory()

    fun getSearchHistory(): ArrayList<Track>

    fun addTrack(track: Track)

    fun clearHistory()

    fun arrayListToJson(list: ArrayList<Track>): String

    fun jsonToArrayList(json: String): ArrayList<Track>

    fun isSearchHistoryNullOrEmpty(): Boolean
}