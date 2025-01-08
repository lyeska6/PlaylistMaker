package com.example.playlistmaker.ui.searchPage

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.ui.audioplayerPage.AudioplayerActivity

class SearchHistoryAdapter(
    val context: Context,
    val searchHistoryInteractor: SearchHistoryInteractor
) :
    RecyclerView.Adapter<TrackViewHolder>() {

    private var isClickAllowed = true

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return searchHistoryInteractor.getSearchHistory().size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(searchHistoryInteractor.getSearchHistory()[position])
        holder.itemView.setOnClickListener {
            if (clickDebounce()) {
                val playerIntent = Intent(context, AudioplayerActivity::class.java)
                searchHistoryInteractor.addTrack(searchHistoryInteractor.getSearchHistory()[position])
                this.notifyDataSetChanged()
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