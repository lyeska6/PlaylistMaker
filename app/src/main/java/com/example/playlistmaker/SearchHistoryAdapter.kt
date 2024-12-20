package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson

private var isClickAllowed = true

private val handler = Handler(Looper.getMainLooper())

class SearchHistoryAdapter(val context: Context, val searchHistory: SearchHistory): RecyclerView.Adapter<TrackViewHolder>() {

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
        searchHistory.sharedPrefs.registerOnSharedPreferenceChangeListener(listener)

        holder.bind(searchHistory.historyTrackArray[position])
        holder.itemView.setOnClickListener {
            if (clickDebounce()) {
                searchHistory.sharedPrefs.edit()
                    .putString(
                        KEY_CHOSEN_TRACK,
                        Gson().toJson(searchHistory.historyTrackArray[position])
                    )
                    .apply()
                val playerIntent = Intent(context, AudioplayerActivity::class.java)
                searchHistory.addTrack(searchHistory.historyTrackArray[position])
                context.startActivity(playerIntent)
            }
        }
    }

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}