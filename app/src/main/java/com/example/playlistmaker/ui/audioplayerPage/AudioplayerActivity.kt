package com.example.playlistmaker.ui.audioplayerPage

import android.content.Context
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
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.AudioPlayerInteractor
import java.text.SimpleDateFormat
import java.util.Locale

class AudioplayerActivity : AppCompatActivity() {

    private var mainHandler: Handler = Handler(Looper.getMainLooper())
    private lateinit var playButton: ImageButton
    private lateinit var currentTiming: TextView
    private lateinit var setTimingRunnable: Runnable

    private val audioPlayerInteractor = Creator.provideAudioPlayerInteractor()

    private val pauseConsumer = object : AudioPlayerInteractor.Consumer {
        override fun consume() {
            pausePlayerView()
        }
    }

    private val startConsumer = object : AudioPlayerInteractor.Consumer {
        override fun consume() {
            startPlayerView()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)
        window.statusBarColor = ContextCompat.getColor(this, R.color.screen_color)

        val track = audioPlayerInteractor.getTrack()

        val backBut = findViewById<ImageView>(R.id.buttonBack)
        backBut.setOnClickListener {
            onBackPressed()
        }

        fun dpToPx(dp: Float, context: Context): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics
            ).toInt()
        }

        val trackCoverView = findViewById<ImageView>(R.id.trackCoverView)
        Glide.with(this).load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
            .transform(RoundedCorners(dpToPx(8F, this)))
            .placeholder(R.drawable.big_placeholder_trackcover).into(trackCoverView)

        val trackNameView = findViewById<TextView>(R.id.trackName)
        trackNameView.text = track.trackName

        val artistNameView = findViewById<TextView>(R.id.trackArtist)
        artistNameView.text = track.artistName

        val durationView = findViewById<TextView>(R.id.playerDuration)
        durationView.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)

        val albumNameView = findViewById<TextView>(R.id.playerAlbum)
        albumNameView.text = track.collectionName

        val yearView = findViewById<TextView>(R.id.playerYear)
        yearView.text = track.releaseDate.substring(0, 4)

        val genreView = findViewById<TextView>(R.id.playerGenre)
        genreView.text = track.primaryGenreName

        val countryView = findViewById<TextView>(R.id.playerCountry)
        countryView.text = track.country

        playButton = findViewById(R.id.playButton)

        audioPlayerInteractor.preparePlayer(track,
            preparedConsumer = object : AudioPlayerInteractor.Consumer {
                override fun consume() {
                    playButton.isEnabled = true
                }
            },
            completionConsumer = object : AudioPlayerInteractor.Consumer {
                override fun consume() {
                    playButton.setImageResource(R.drawable.play_button)
                    mainHandler.removeCallbacks(setTimingRunnable)
                    currentTiming.text = getString(R.string.zero_current_timing)
                }
            })

        playButton.setOnClickListener {
            audioPlayerInteractor.startOrPausePlayer(pauseConsumer, startConsumer)
        }

        currentTiming = findViewById<TextView>(R.id.currentTiming)
        setTimingRunnable = Runnable { setCurrentTiming() }
    }

    override fun onPause() {
        super.onPause()
        audioPlayerInteractor.pausePlayer(pauseConsumer)
    }

    override fun onDestroy() {
        super.onDestroy()
        audioPlayerInteractor.releasePlayer()
        mainHandler.removeCallbacks(setTimingRunnable)
    }

    private fun startPlayerView() {
        playButton.setImageResource(R.drawable.pause_button)
        mainHandler.postDelayed(setTimingRunnable, 400L)
    }

    private fun pausePlayerView() {
        playButton.setImageResource(R.drawable.play_button)
        mainHandler.removeCallbacks(setTimingRunnable)
    }

    private fun setCurrentTiming() {
        val time = SimpleDateFormat(
            "mm:ss", Locale.getDefault()
        ).format(audioPlayerInteractor.getCurrentTiming())
        currentTiming.text = time
        mainHandler.postDelayed(setTimingRunnable, 300L)
    }
}