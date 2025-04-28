package com.example.playlistmaker.ui.playlists.activity

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.playlistmaker.databinding.ItemPlaylistBigBinding
import com.example.playlistmaker.domain.playlists.model.Playlist

class PlaylistsAdapter(
    private val playlists: ArrayList<Playlist>,
    private val clickListener: (id: Long) -> Unit
): Adapter<PlaylistsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistsViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)
        return PlaylistsViewHolder(
            ItemPlaylistBigBinding.inflate(layoutInspector, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: PlaylistsViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener {
           clickListener(playlists[position].id)
        }
    }


}