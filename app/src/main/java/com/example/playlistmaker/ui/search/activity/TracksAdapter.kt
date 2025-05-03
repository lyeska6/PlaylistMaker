package com.example.playlistmaker.ui.search.activity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.ItemTrackBinding
import com.example.playlistmaker.domain.search.model.Track

class TracksAdapter(
    private val tracks: ArrayList<Track>,
    private val isLongClickable: Boolean,
    private val clickListener: (Track) -> Unit,
    private val longClickListener: (Track) -> Boolean
): RecyclerView.Adapter<TrackViewHolder>() {

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
            clickListener(tracks[position])
        }
        holder.itemView.isLongClickable = isLongClickable
        if (isLongClickable) {
            holder.itemView.setOnLongClickListener {
                longClickListener(tracks[position])
            }
        }
    }
}