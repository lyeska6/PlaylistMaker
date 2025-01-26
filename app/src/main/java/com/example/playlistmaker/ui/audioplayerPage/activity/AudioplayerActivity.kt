package com.example.playlistmaker.ui.audioplayerPage.activity

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityAudioplayerBinding
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.audioplayerPage.view_model.AudioplayerViewModel
import com.example.playlistmaker.ui.audioplayerPage.view_model.PlayerState
import java.text.SimpleDateFormat
import java.util.Locale

class AudioplayerActivity : AppCompatActivity() {

    private lateinit var viewModel: AudioplayerViewModel
    private lateinit var binding: ActivityAudioplayerBinding

    private var mainHandler: Handler = Handler(Looper.getMainLooper())
    private lateinit var setTimingRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAudioplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.screen_color)

        viewModel = ViewModelProvider(this, AudioplayerViewModel.getViewModelFactory())[AudioplayerViewModel::class.java]

        viewModel.preparePlayer()

        viewModel.getTrackLiveData().observe(this) { track ->
            binding.trackName.text = track.trackName
            binding.trackArtist.text = track.artistName
            binding.trackAlbum.text = track.collectionName
            binding.trackYear.text = track.releaseDate.substring(0, 4)
            binding.trackGenre.text = track.primaryGenreName
            binding.trackCountry.text = track.country
            binding.trackDuration.text =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
            Glide.with(this)
                .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
                .transform(RoundedCorners(dpToPx(8F, this)))
                .placeholder(R.drawable.big_placeholder_trackcover)
                .into(binding.trackCoverView)
        }

        viewModel.getCurrentTimingLiveData().observe(this){ timing ->
            val time = SimpleDateFormat(
                "mm:ss", Locale.getDefault()
            ).format(timing)
            binding.currentTiming.text = time
            mainHandler.postDelayed(setTimingRunnable, 300L)
        }

        viewModel.getPlayerStateLiveData().observe(this){ state ->
            when (state) {
                PlayerState.StateDefault -> {

                }
                PlayerState.StatePrepared -> {
                    binding.playButton.setImageResource(R.drawable.play_button)
                    mainHandler.removeCallbacks(setTimingRunnable)
                    viewModel.setCurrentTiming()
                    binding.playButton.isEnabled = true
                }
                PlayerState.StatePlaying ->{
                    startPlayerView()
                }
                PlayerState.StatePaused -> {
                    pausePlayerView()
                }
            }
        }

        binding.playButton.setOnClickListener {
            viewModel.startOrPausePlayer()
        }

        setTimingRunnable = Runnable {setCurrentTiming()}

        binding.buttonBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun startPlayerView() {
        binding.playButton.setImageResource(R.drawable.pause_button)
        mainHandler.postDelayed(setTimingRunnable, 300L)
    }

    private fun pausePlayerView() {
        binding.playButton.setImageResource(R.drawable.play_button)
        mainHandler.removeCallbacks(setTimingRunnable)
    }

    private fun setCurrentTiming() {
        viewModel.setCurrentTiming()
        mainHandler.postDelayed(setTimingRunnable, 300L)
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics
        ).toInt()
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.releasePlayer()
        mainHandler.removeCallbacks(setTimingRunnable)
    }
}