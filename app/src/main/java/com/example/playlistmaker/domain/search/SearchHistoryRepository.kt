package com.example.playlistmaker.domain.search

import com.example.playlistmaker.domain.search.model.Track

interface SearchHistoryRepository {

    fun getSearchHistory(elseList: ArrayList<Track>): ArrayList<Track>

    fun setSearchHistory(list: ArrayList<Track>)

    fun setChosenTrack(track: Track)

    fun getChosenTrack(): Track
}