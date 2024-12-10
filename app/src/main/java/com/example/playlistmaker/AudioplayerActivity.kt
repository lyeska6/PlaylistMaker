package com.example.playlistmaker

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.widget.ImageButton
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

    private var playerState = STATE_DEFAULT
    private val player = MediaPlayer()

    private lateinit var mainHandler : Handler
    private lateinit var playButton : ImageButton
    private lateinit var currentTiming : TextView
    private lateinit var setTimingRunnable : Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)
        window.statusBarColor = ContextCompat.getColor(this, R.color.screen_color)

        val sharedPrefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)
        val trackJson = sharedPrefs.getString(KEY_CHOSEN_TRACK, null)
        val track = Gson().fromJson(trackJson, Track::class.java)

        val backBut = findViewById<ImageView>(R.id.buttonBack)
        backBut.setOnClickListener {
            onBackPressed()
        }

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

        playButton = findViewById(R.id.playButton)

        preparePlayer(track)

        playButton.setOnClickListener {
            when (playerState) {
                STATE_PLAYING -> pausePlayer()
                STATE_PREPARED,
                STATE_PAUSED -> startPlayer()
            }
        }

        currentTiming = findViewById<TextView>(R.id.currentTiming)
        mainHandler = Handler(Looper.getMainLooper())
        setTimingRunnable = Runnable { setCurrentTiming() }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
        mainHandler.removeCallbacks(setTimingRunnable)
    }

    private fun preparePlayer(track : Track){
        player.setDataSource(track.previewUrl)
        player.prepareAsync()
        player.setOnPreparedListener {
            playButton.isEnabled = true
            playerState = STATE_PREPARED
        }
        player.setOnCompletionListener {
            playButton.setImageResource(R.drawable.play_button)
            playerState = STATE_PREPARED
            mainHandler.removeCallbacks(setTimingRunnable)
            currentTiming.text = getString(R.string.zero_current_timing)
        }
    }

    private fun startPlayer(){
        player.start()
        playButton.setImageResource(R.drawable.pause_button)
        playerState = STATE_PLAYING
        mainHandler.postDelayed(setTimingRunnable, 400L)
    }

    private fun pausePlayer(){
        player.pause()
        playButton.setImageResource(R.drawable.play_button)
        playerState = STATE_PAUSED
        mainHandler.removeCallbacks(setTimingRunnable)
    }

    private fun setCurrentTiming(){
        val time = SimpleDateFormat("mm:ss", Locale.getDefault()).format(player.currentPosition)
        currentTiming.text = time
        mainHandler.postDelayed(setTimingRunnable, 300L)
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }
}