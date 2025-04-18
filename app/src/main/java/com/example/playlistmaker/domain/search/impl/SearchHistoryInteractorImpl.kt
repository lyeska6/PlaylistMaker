package com.example.playlistmaker.domain.search.impl

import com.example.playlistmaker.data.db.AppDataBase
import com.example.playlistmaker.domain.search.SearchHistoryInteractor
import com.example.playlistmaker.domain.search.SearchHistoryRepository
import com.example.playlistmaker.domain.search.model.Track

class SearchHistoryInteractorImpl(
    private val repository: SearchHistoryRepository
): SearchHistoryInteractor {

    private val emptyArray = arrayListOf<Track>()

    override fun getSearchHistory(): ArrayList<Track> {
        return repository.getSearchHistory(emptyArray)
    }

    override fun addTrack(track: Track) {
        repository.setChosenTrack(track)

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

        repository.setSearchHistory(historyTrackArray)
    }

    override fun clearHistory() {
        repository.setSearchHistory(emptyArray)
    }

    companion object {
        const val SEARCH_HISTORY_LENGTH = 10
    }
}