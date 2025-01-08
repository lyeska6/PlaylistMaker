package com.example.playlistmaker.domain.impl

import com.google.gson.Gson
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SharedPrefsRepository
import com.example.playlistmaker.domain.models.Track

const val SEARCH_HISTORY_LENGTH = 10

class SearchHistoryInteractorImpl(val repository: SharedPrefsRepository) : SearchHistoryInteractor {

    val gson = Gson()
    val emptyArray = arrayListOf<Track>()
    val jsonEmptyArray = arrayListToJson(emptyArray)
    private val jsonHistoryTrackArray = repository.getSearchHistory(jsonEmptyArray)
    var historyTrackArray = jsonToArrayList(jsonHistoryTrackArray)

    override fun setSearchHistory() {
        historyTrackArray = jsonToArrayList(repository.getSearchHistory(jsonEmptyArray))
    }

    override fun getSearchHistory(): ArrayList<Track> {
        return historyTrackArray
    }

    override fun addTrack(track: Track) {
        val jsonTrack = gson.toJson(track)
        repository.setChosenTrack(jsonTrack)

        if (!historyTrackArray.isNullOrEmpty()) {
            val idList = arrayListOf<String>()
            for (t in historyTrackArray) {
                idList.add(t.trackId)
            }
            if (track.trackId in idList) {
                val ind = idList.indexOf(track.trackId)
                historyTrackArray.removeAt(ind)
            } else if (historyTrackArray.size == SEARCH_HISTORY_LENGTH) {
                historyTrackArray.removeAt(SEARCH_HISTORY_LENGTH - 1)
            }
            historyTrackArray.add(0, track)
        } else {
            historyTrackArray = arrayListOf(track)
        }

        val json = arrayListToJson(historyTrackArray)
        historyTrackArray = jsonToArrayList(repository.setSearchHistory(json))
    }

    override fun clearHistory() {
        historyTrackArray = emptyArray
        repository.setSearchHistory(jsonEmptyArray)
    }

    override fun arrayListToJson(arrayList: ArrayList<Track>): String {
        return gson.toJson(arrayList.toTypedArray())
    }

    override fun jsonToArrayList(json: String): ArrayList<Track> {
        return ArrayList(
            gson.fromJson(
                json,
                Array<Track>::class.java
            ).asList()
        )
    }

    override fun isSearchHistoryNullOrEmpty(): Boolean {
        val currentSearchHistory = jsonToArrayList(repository.getSearchHistory(jsonEmptyArray))
        return currentSearchHistory.isNullOrEmpty()
    }
}