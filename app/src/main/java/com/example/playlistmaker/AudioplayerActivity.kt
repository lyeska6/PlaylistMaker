package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class AudioplayerActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)
        window.statusBarColor = ContextCompat.getColor(this, R.color.screen_color)

        val backBut = findViewById<ImageView>(R.id.buttonBack)
        backBut.setOnClickListener {
            onBackPressed()
        }

        val sharedPrefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)
        val trackJson = sharedPrefs.getString(KEY_CHOSEN_TRACK, null)
        val track = Gson().fromJson(trackJson, Track::class.java)

        fun dpToPx(dp: Float, context: Context): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.resources.displayMetrics).toInt()
        }

        val trackCoverView = findViewById<ImageView>(R.id.trackCoverView)
        Glide.with(this).load(track.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg"))
            .transform(RoundedCorners(dpToPx(8F, this)))
            .placeholder(R.drawable.big_placeholder_trackcover)
            .into(trackCoverView)

        val trackNameView = findViewById<TextView>(R.id.trackName)
        trackNameView.text = track.trackName

        val artistNameView = findViewById<TextView>(R.id.trackArtist)
        artistNameView.text = track.artistName

        val durationView = findViewById<TextView>(R.id.playerDuration)
        durationView.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)

        val albumNameView = findViewById<TextView>(R.id.playerAlbum)
        albumNameView.text = track.collectionName

        val yearView = findViewById<TextView>(R.id.playerYear)
        yearView.text = track.releaseDate.substring(0,4)

        val genreView = findViewById<TextView>(R.id.playerGenre)
        genreView.text = track.primaryGenreName

        val countryView = findViewById<TextView>(R.id.playerCountry)
        countryView.text = track.country
    }
}