package com.example.playlistmaker

import android.app.Application.MODE_PRIVATE
import com.google.gson.Gson

import android.content.SharedPreferences
import android.util.Log

const val SEARCH_HISTORY_LENGTH = 10

public class SearchHistory(val sharedPrefs : SharedPreferences) {

    val gson = Gson()
    val emptyArray = arrayListOf<Track>()
    val jsonEmptyArray = arrayListToJson(emptyArray)
    private val jsonHistoryTrackArray = getSharedPrefs(KEY_SEARCH_HISTORY_LIST)
    var historyTrackArray = jsonToArrayList(jsonHistoryTrackArray)

    fun setArray() {
        historyTrackArray = jsonToArrayList(getSharedPrefs(KEY_SEARCH_HISTORY_LIST))
    }

    fun setArray(json : String) {
        historyTrackArray = jsonToArrayList(json)
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
            } else if (historyTrackArray.size == SEARCH_HISTORY_LENGTH) {
                historyTrackArray.removeAt(SEARCH_HISTORY_LENGTH - 1)
            }
            historyTrackArray.add(0, track)
        } else {
            historyTrackArray = arrayListOf(track)
        }

        val json = arrayListToJson(historyTrackArray)
        setSharedPrefs(json)
        historyTrackArray = jsonToArrayList(getSharedPrefs(KEY_SEARCH_HISTORY_LIST))
    }

    fun clearHistory() {
        historyTrackArray = emptyArray
        setSharedPrefs(jsonEmptyArray)
    }

    fun arrayListToJson(arrayList : ArrayList<Track>) : String{
        return gson.toJson(arrayList.toTypedArray())
    }

    fun jsonToArrayList(json : String) : ArrayList<Track> {
        return ArrayList(
            gson.fromJson(
                json,
                Array<Track>::class.java
            ).asList())
    }

    fun getSharedPrefs(key : String) : String {
        val str = sharedPrefs.getString(key, jsonEmptyArray)
        if (str == null) {
            return jsonEmptyArray
        } else{
            return str
        }
    }

    fun setSharedPrefs(str : String) {
        sharedPrefs.edit()
            .putString(KEY_SEARCH_HISTORY_LIST, str)
            .apply()
    }
}