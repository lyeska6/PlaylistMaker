package com.example.playlistmaker.ui.madiatec.activity

import android.content.Context
import android.net.Uri
import android.util.Log
import android.util.TypedValue
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ItemPlaylistBigBinding
import com.example.playlistmaker.domain.playlists.model.Playlist
import java.io.File

class PlaylistsViewHolder(
    private val binding: ItemPlaylistBigBinding
): ViewHolder(binding.root) {

    fun bind(playlist: Playlist) {
        binding.playlistName.text = playlist.name
        binding.playlistSize.text = getPlaylistSizeText(playlist.size)
        Glide.with(itemView)
            .load(getPlaylistCoverUri(playlist))
            .transform(CenterCrop(), RoundedCorners(dpToPx(8F, itemView.context)))
            .placeholder(R.drawable.big_placeholder_trackcover)
            .into(binding.playlistCover)
    }

    private fun getPlaylistSizeText(size: Int): String {
        var sizeText = "$size "
        sizeText += if (size in 11..14) {
            "треков"
        } else if (size %10 == 1) {
            "трек"
        } else if (size %10 in 2..4) {
            "трека"
        } else {
            "треков"
        }
        return sizeText
    }

    private fun getPlaylistCoverUri(playlist: Playlist): Uri{
        val file = File(itemView.context.getDir(playlist.coverPath, Context.MODE_PRIVATE), playlist.name)
        return file.toUri()
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }
}