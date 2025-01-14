package com.example.playlistmaker.data

import com.example.playlistmaker.Creator
import com.example.playlistmaker.KEY_CHOSEN_TRACK
import com.example.playlistmaker.KEY_SEARCH_HISTORY_LIST
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson

class SearchHistoryRepositoryImpl : SearchHistoryRepository {

    private val sharedPrefs = Creator.getSharedPrefs()

    override fun getSearchHistory(elseString: String): String {
        val response = sharedPrefs.getString(
            KEY_SEARCH_HISTORY_LIST,
            elseString
        ).toString()
        return response
    }

    override fun setSearchHistory(jsonList: String): String {
        sharedPrefs.edit()
            .putString(KEY_SEARCH_HISTORY_LIST, jsonList)
            .apply()
        return jsonList
    }

    override fun setChosenTrack(jsonTrack: String) {
        sharedPrefs.edit()
            .putString(KEY_CHOSEN_TRACK, jsonTrack)
            .apply()
    }

    override fun getChosenTrack(): Track {
        val jsonTrack = sharedPrefs.getString(KEY_CHOSEN_TRACK, null)
        return Gson().fromJson(jsonTrack, Track::class.java)
    }
}