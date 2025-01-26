package com.example.playlistmaker.ui.search.activity

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ItemTrackBinding
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.audioplayerPage.activity.AudioplayerActivity

class SearchedTracksAdapter(
    private val context: Context,
    private val tracks: ArrayList<Track>,
    val addTrack: (Track) -> Unit
) : RecyclerView.Adapter<TrackViewHolder>() {

    private var isClickAllowed = true

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)
        return TrackViewHolder(
            ItemTrackBinding.inflate(layoutInspector, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            if (clickDebounce()) {
                val playerIntent = Intent(context, AudioplayerActivity::class.java)
                addTrack(tracks[position])
                context.startActivity(playerIntent)
            }
        }
    }

    private fun clickDebounce(): Boolean {
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