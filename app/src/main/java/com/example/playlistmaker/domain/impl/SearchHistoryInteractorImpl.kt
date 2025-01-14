package com.example.playlistmaker.domain.impl

import com.google.gson.Gson
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track

class SearchHistoryInteractorImpl(val repository: SearchHistoryRepository) : SearchHistoryInteractor {

    val gson = Gson()
    val emptyArray = arrayListOf<Track>()
    val jsonEmptyArray = arrayListToJson(emptyArray)

    override fun getSearchHistory(): ArrayList<Track> {
        return jsonToArrayList(repository.getSearchHistory(jsonEmptyArray))
    }

    override fun addTrack(track: Track) {
        val jsonTrack = gson.toJson(track)
        repository.setChosenTrack(jsonTrack)

        var historyTrackArray = getSearchHistory()

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
        repository.setSearchHistory(json)
    }

    override fun clearHistory() {
        repository.setSearchHistory(jsonEmptyArray)
    }

    private fun arrayListToJson(arrayList: ArrayList<Track>): String {
        return gson.toJson(arrayList.toTypedArray())
    }

    private fun jsonToArrayList(json: String): ArrayList<Track> {
        return ArrayList(
            gson.fromJson(
                json,
                Array<Track>::class.java
            ).asList()
        )
    }

    companion object {
        const val SEARCH_HISTORY_LENGTH = 10
    }
}