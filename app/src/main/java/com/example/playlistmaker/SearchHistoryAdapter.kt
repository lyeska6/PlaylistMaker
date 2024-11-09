package com.example.playlistmaker

import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class SearchHistoryAdapter(val sharedPrefs : SharedPreferences, val searchHistory: SearchHistory): RecyclerView.Adapter<TrackViewHolder>() {

    lateinit var listener : SharedPreferences.OnSharedPreferenceChangeListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return searchHistory.historyTrackArray.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        listener = SharedPreferences.OnSharedPreferenceChangeListener{ sharedPreferences, key ->
            if (key == KEY_SEARCH_HISTORY_LIST) {
                searchHistory.setArray()
                this.notifyDataSetChanged()
            }
        }
        sharedPrefs.registerOnSharedPreferenceChangeListener(listener)

        holder.bind(searchHistory.historyTrackArray[position])
        holder.itemView.setOnClickListener {
            searchHistory.addTrack(searchHistory.historyTrackArray[position])
        }
    }
}