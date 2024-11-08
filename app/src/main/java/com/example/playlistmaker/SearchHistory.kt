package com.example.playlistmaker

import android.app.Application.MODE_PRIVATE
import com.google.gson.Gson

import android.content.SharedPreferences

public class SearchHistory(val sharedPrefs : SharedPreferences) {

    val jsonHistoryTrackArray = sharedPrefs.getString(KEY_SEARCH_HISTORY_LIST, null)
    var historyTrackArray : ArrayList<Track> = if (jsonHistoryTrackArray != null) ArrayList(Gson().fromJson(jsonHistoryTrackArray, Array<Track>::class.java).asList()) else arrayListOf()

    fun setArray() : ArrayList<Track> {
        if (sharedPrefs.getString(KEY_SEARCH_HISTORY_LIST, null).isNullOrEmpty()) {
            historyTrackArray = arrayListOf()
        } else {
            historyTrackArray = ArrayList(
                Gson().fromJson(
                    sharedPrefs.getString(KEY_SEARCH_HISTORY_LIST, null),
                    Array<Track>::class.java
                ).asList()
            )
        }
        return historyTrackArray
    }

    fun addTrack(track: Track){
        if (!historyTrackArray.isNullOrEmpty()) {
            val idList = arrayListOf<String>()
            for (t in historyTrackArray) {
                idList.add(t.trackId)
            }
            if (track.trackId in idList) {
                val ind = idList.indexOf(track.trackId)
                historyTrackArray.removeAt(ind)
            } else if (historyTrackArray.size == 10) {
                historyTrackArray.removeAt(9)
            }
            historyTrackArray.add(0, track)
        } else {
            historyTrackArray = arrayListOf(track)
        }

        val json = Gson().toJson(historyTrackArray.toTypedArray())
        sharedPrefs.edit()
            .putString(KEY_SEARCH_HISTORY_LIST, json)
            .apply()
    }

    fun clearHistory() {
        historyTrackArray = arrayListOf()
        sharedPrefs.edit()
            .putString(KEY_SEARCH_HISTORY_LIST, null)
            .apply()
    }
}