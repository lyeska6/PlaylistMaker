package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface SearchHistoryRepository {

    fun getSearchHistory(elseList: ArrayList<Track>): ArrayList<Track>

    fun setSearchHistory(list: ArrayList<Track>)

    fun setChosenTrack(track: Track)

    fun getChosenTrack(): Track
}