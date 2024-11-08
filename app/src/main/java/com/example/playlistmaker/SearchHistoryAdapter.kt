package com.example.playlistmaker

import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class SearchHistoryAdapter(var tracks : ArrayList<Track>, val sharedPrefs : SharedPreferences): RecyclerView.Adapter<TrackViewHolder>() {

    lateinit var listener : SharedPreferences.OnSharedPreferenceChangeListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val searchHistoryData = SearchHistory(sharedPrefs)
        listener = SharedPreferences.OnSharedPreferenceChangeListener{ sharedPreferences, key ->
            if (key == KEY_SEARCH_HISTORY_LIST) {
                tracks = searchHistoryData.setArray()
            }
        }
        sharedPrefs.registerOnSharedPreferenceChangeListener(listener)

        holder.itemView.setOnClickListener {
            searchHistoryData.addTrack(tracks[position])
            tracks = searchHistoryData.historyTrackArray
        }
        holder.bind(tracks[position])
    }
}