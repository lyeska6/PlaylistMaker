package com.example.playlistmaker.ui.audioplayer.activity

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityAudioplayerBinding
import com.example.playlistmaker.domain.playlists.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.audioplayer.view_model.AudioplayerViewModel
import com.example.playlistmaker.ui.audioplayer.view_model.PlayerState
import com.example.playlistmaker.ui.playlists.activity.NewPlaylistFragment
import com.example.playlistmaker.utils.debounce
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.ArrayList

class AudioplayerActivity: AppCompatActivity() {

    private val viewModel by viewModel<AudioplayerViewModel>()
    private lateinit var binding: ActivityAudioplayerBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private val onClickDebounce: (Boolean) -> Unit = debounce(CLICK_DEBOUNCE_DELAY, lifecycleScope, false) {
        viewModel.startOrPausePlayer()
    }

    private lateinit var theTrack: Track

    private val playlists = ArrayList<Playlist>()
    private val playlistsAdapter = PlaylistsToAddTrackAdapter(playlists) {playlist ->
        onPlaylistClick(playlist)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.screen_color)

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object: BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.isVisible = false
                    }
                    else -> {
                        binding.overlay.isVisible = true
                    }
                }
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        viewModel.updatePlaylistsList()
                    }
                    else -> {}
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        var isPlayerPrepared = false

        viewModel.getTrackLiveData().observe(this) { track ->
            if (! isPlayerPrepared) {
                viewModel.preparePlayer(track)
                isPlayerPrepared = true
            }
            theTrack = track
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
                binding.likeTrackBut.setImageResource(R.drawable.liked_track_but)
            } else {
                binding.likeTrackBut.setImageResource(R.drawable.unliked_track_but)
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

        viewModel.getPlaylistsListLiveData().observe(this){ list ->
            updateBottomSheet(list)
        }

        viewModel.getAddedToPlaylistToastLiveData().observe(this){ s ->
            Toast.makeText(this, s, Toast.LENGTH_LONG).show()
        }

        binding.playlistsRV.layoutManager = LinearLayoutManager(this)
        binding.playlistsRV.adapter = playlistsAdapter

        binding.addToPlaylistBut.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.newPlaylistBut.setOnClickListener {
            supportFragmentManager.commit {
                add<NewPlaylistFragment>(R.id.addPlaylistFragmentContainerView).addToBackStack("addPlaylistFragment")
            }
            binding.addPlaylistFragmentContainerView.isVisible = true
        }

        supportFragmentManager.registerFragmentLifecycleCallbacks(object: FragmentManager.FragmentLifecycleCallbacks(){
            override fun onFragmentStopped(fm: FragmentManager, f: Fragment) {
                super.onFragmentStopped(fm, f)
                binding.addPlaylistFragmentContainerView.isVisible = false
                viewModel.updatePlaylistsList()
            }
        }, false)

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

    private fun updateBottomSheet(newList: List<Playlist>) {
        playlists.clear()
        playlists.addAll(newList)
        binding.playlistsRV.adapter?.notifyDataSetChanged()
    }

    private fun onPlaylistClick(playlist: Playlist) {
        viewModel.addTrackToPlaylist(playlist, theTrack)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
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