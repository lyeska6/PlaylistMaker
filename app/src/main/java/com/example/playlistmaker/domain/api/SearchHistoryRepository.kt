package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface SearchHistoryRepository {

    fun getSearchHistory(elseString: String): String

    fun setSearchHistory(jsonList: String): String

    fun setChosenTrack(jsonTrack: String)

    fun getChosenTrack(): Track
}