package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson

private var isClickAllowed = true

private val handler = Handler(Looper.getMainLooper())

class SearchedTracksAdapter(val context: Context, val tracks : ArrayList<Track>, val searchHistory: SearchHistory): RecyclerView.Adapter<TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            if (clickDebounce()) {
                searchHistory.sharedPrefs.edit()
                    .putString(KEY_CHOSEN_TRACK, Gson().toJson(tracks[position]))
                    .apply()
                val playerIntent = Intent(context, AudioplayerActivity::class.java)
                searchHistory.addTrack(tracks[position])
                searchHistory.setArray()
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
