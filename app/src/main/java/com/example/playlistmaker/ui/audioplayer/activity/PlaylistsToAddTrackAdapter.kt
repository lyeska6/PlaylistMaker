package com.example.playlistmaker.ui.audioplayer.activity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.playlistmaker.databinding.ItemPlaylistSmallBinding
import com.example.playlistmaker.domain.playlists.model.Playlist
import com.example.playlistmaker.domain.search.model.Track

class PlaylistsToAddTrackAdapter(
    private val playlists: ArrayList<Playlist>,
    private val clickListener: (Playlist) -> Unit
): Adapter<PlaylistsToAddTrackViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistsToAddTrackViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)
        return PlaylistsToAddTrackViewHolder(
            ItemPlaylistSmallBinding.inflate(layoutInspector, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: PlaylistsToAddTrackViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener {
            clickListener(playlists[position])
        }
    }

}