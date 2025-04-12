package com.example.playlistmaker.data.search.impl

import android.content.SharedPreferences
import com.example.playlistmaker.KEY_CHOSEN_TRACK
import com.example.playlistmaker.KEY_SEARCH_HISTORY_LIST
import com.example.playlistmaker.data.db.AppDataBase
import com.example.playlistmaker.domain.search.SearchHistoryRepository
import com.example.playlistmaker.domain.search.model.Track
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking

class SearchHistoryRepositoryImpl(
    private val sharedPrefs: SharedPreferences,
    private val gson: Gson,
    private val dataBase: AppDataBase
) : SearchHistoryRepository {

    override fun getSearchHistory(elseList: ArrayList<Track>): ArrayList<Track> {
        val jsonElseList = arrayListToJson(elseList)
        val response = sharedPrefs.getString(
            KEY_SEARCH_HISTORY_LIST,
            jsonElseList
        ).toString()
        var idList = emptyList<String>()
        runBlocking {
            idList = dataBase.getTrackDao().getFavIds()
        }
        return ArrayList(jsonToArrayList(response).map { track ->
            track.apply { isFavourite = track.trackId in idList }
        })
    }

    override fun setSearchHistory(list: ArrayList<Track>) {
        val jsonList = arrayListToJson(list)
        sharedPrefs.edit()
            .putString(KEY_SEARCH_HISTORY_LIST, jsonList)
            .apply()
    }

    override fun setChosenTrack(track: Track) {
        var idList = emptyList<String>()
        runBlocking {
            idList = dataBase.getTrackDao().getFavIds()
        }
        track.isFavourite = track.trackId in idList
        val jsonTrack = gson.toJson(track, Track::class.java)
        sharedPrefs.edit()
            .putString(KEY_CHOSEN_TRACK, jsonTrack)
            .apply()
    }

    override fun getChosenTrack(): Track {
        val jsonTrack = sharedPrefs.getString(KEY_CHOSEN_TRACK, null)
        return gson.fromJson(jsonTrack, Track::class.java)
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
}