package com.example.playlistmaker.ui.audioplayer.activity

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityAudioplayerBinding
import com.example.playlistmaker.ui.audioplayer.view_model.AudioplayerViewModel
import com.example.playlistmaker.ui.audioplayer.view_model.PlayerState
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class AudioplayerActivity: AppCompatActivity() {

    private val viewModel by viewModel<AudioplayerViewModel>()
    private lateinit var binding: ActivityAudioplayerBinding

    private val handler = Handler(Looper.getMainLooper())

    private var isClickAllowed = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.screen_color)

        viewModel.getTrackLiveData().observe(this) { track ->
            viewModel.preparePlayer(track)
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

        viewModel.getPlayerStateLiveData().observe(this){ state ->
            when (state) {
                PlayerState.StateDefault -> {

                }
                PlayerState.StatePrepared -> {
                    preparedPlayerView()
                }
                is PlayerState.StatePlaying -> {
                    startPlayerView(state.timing)
                }
                PlayerState.StatePaused -> {
                    pausePlayerView()
                }
            }
        }

        binding.playButton.setOnClickListener {
            if (clickDebounce()) {
                viewModel.startOrPausePlayer()
            }
        }

        binding.buttonBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun startPlayerView(timing: Int) {
        binding.playButton.setImageResource(R.drawable.pause_button)
        val time = SimpleDateFormat(
            "mm:ss", Locale.getDefault()
        ).format(timing)
        binding.currentTiming.text = time
    }

    private fun pausePlayerView() {
        binding.playButton.setImageResource(R.drawable.play_button)
    }

    private fun preparedPlayerView() {
        binding.playButton.setImageResource(R.drawable.play_button)
        binding.currentTiming.text = getString(R.string.zero_current_timing)
        binding.playButton.isEnabled = true
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics
        ).toInt()
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
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
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}