package com.example.playlistmaker

import android.content.Context
import android.util.TypedValue
import android.view.RoundedCorner
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val trackName = itemView.findViewById<TextView>(R.id.track_name)
    private val trackArtist = itemView.findViewById<TextView>(R.id.track_artist)
    private val trackTime = itemView.findViewById<TextView>(R.id.track_time)
    private val trackCover = itemView.findViewById<ImageView>(R.id.track_cover)

    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }

    fun bind(track: Track) {
        trackName.text = track.trackName
        trackArtist.text = track.artistName
        trackTime.text = track.trackTime
        Glide.with(itemView).load(track.artworkUrl100)
            .fitCenter()
            .transform(RoundedCorners(dpToPx(2F, itemView.context)))
            .placeholder(R.drawable.placeholder_trackcover)
            .into(trackCover)
        trackName.requestLayout()
        trackArtist.requestLayout()
        trackTime.requestLayout()
    }
}