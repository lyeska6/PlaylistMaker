package com.example.playlistmaker.ui.search.activity

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ItemTrackBinding
import com.example.playlistmaker.domain.search.model.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(private val binding: ItemTrackBinding) : RecyclerView.ViewHolder(binding.root) {

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

    fun bind(track: Track) {
        binding.trackName.text = track.trackName
        binding.trackArtist.text = track.artistName
        binding.trackTime.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        Glide.with(itemView).load(track.artworkUrl100)
            .transform(RoundedCorners(dpToPx(2F, itemView.context)))
            .placeholder(R.drawable.placeholder_trackcover)
            .into(binding.trackCover)
        binding.trackName.requestLayout()
        binding.trackArtist.requestLayout()
        binding.trackTime.requestLayout()
    }
}