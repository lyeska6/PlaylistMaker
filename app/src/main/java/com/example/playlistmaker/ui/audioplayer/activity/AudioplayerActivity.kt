package com.example.playlistmaker.ui.audioplayer.activity

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityAudioplayerBinding
import com.example.playlistmaker.ui.audioplayer.view_model.AudioplayerViewModel
import com.example.playlistmaker.ui.audioplayer.view_model.PlayerState
import com.example.playlistmaker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class AudioplayerActivity: AppCompatActivity() {

    private val viewModel by viewModel<AudioplayerViewModel>()
    private lateinit var binding: ActivityAudioplayerBinding

    private val onClickDebounce: (Boolean) -> Unit = debounce(CLICK_DEBOUNCE_DELAY, lifecycleScope, false) {
        viewModel.startOrPausePlayer()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.screen_color)

        var isPlayerPrepared = false

        viewModel.getTrackLiveData().observe(this) { track ->
            if (! isPlayerPrepared) {
                viewModel.preparePlayer(track)
                isPlayerPrepared = true
            }
            binding.trackName.text = track.trackName
            binding.trackArtist.text = track.artistName
            binding.trackAlbum.text = track.collectionName
            binding.trackYear.text = track.releaseDate.substring(0, 4)
            binding.trackGenre.text = track.primaryGenreName
            binding.trackCountry.text = track.country
            Glide.with(this)
                .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
                .transform(RoundedCorners(dpToPx(8F, this)))
                .placeholder(R.drawable.big_placeholder_trackcover)
                .into(binding.trackCoverView)
            if (track.isFavourite) {
                Glide.with(this)
                    .load(R.drawable.liked_track_but)
                    .into(binding.likeTrackBut)
            } else {
                Glide.with(this)
                    .load(R.drawable.unliked_track_but)
                    .into(binding.likeTrackBut)
            }
        }

        viewModel.getPlayerStateLiveData().observe(this){ state ->
            when (state) {
                is PlayerState.StateDefault -> {
                    defaultPlayerView()
                }
                is PlayerState.StatePrepared -> {
                    preparedPlayerView()
                }
                is PlayerState.StatePlaying -> {
                    startPlayerView(state.timing)
                }
                is PlayerState.StatePaused -> {
                    pausePlayerView(state.timing)
                }
            }
        }

        binding.playButton.setOnClickListener {
            onClickDebounce(true)
        }

        binding.likeTrackBut.setOnClickListener {
            viewModel.onFavouriteClicked()
        }

        binding.buttonBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun defaultPlayerView() {
        binding.playButton.setImageResource(R.drawable.play_button)
        binding.currentTiming.text = getString(R.string.zero_current_timing)
        binding.playButton.isEnabled = false
    }

    private fun preparedPlayerView() {
        binding.playButton.setImageResource(R.drawable.play_button)
        binding.currentTiming.text = getString(R.string.zero_current_timing)
        binding.playButton.isEnabled = true
    }

    private fun startPlayerView(timing: String) {
        binding.playButton.setImageResource(R.drawable.pause_button)
        binding.currentTiming.text = timing
    }

    private fun pausePlayerView(timing: String) {
        binding.playButton.setImageResource(R.drawable.play_button)
        binding.currentTiming.text = timing
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
        viewModel.onDestroy()
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 300L
    }
}