package com.example.playlistmaker.ui.playlists.activity

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.domain.playlists.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.playlists.view_model.NewPlaylistViewModel
import com.example.playlistmaker.ui.playlists.view_model.PlaylistState
import com.example.playlistmaker.ui.playlists.view_model.PlaylistViewModel
import com.example.playlistmaker.ui.root.RootActivity
import com.example.playlistmaker.ui.search.activity.TracksAdapter
import com.example.playlistmaker.utils.debounce
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Locale

class PlaylistFragment: Fragment() {

    private val viewModel by viewModel<PlaylistViewModel>()
    private lateinit var binding: FragmentPlaylistBinding
    private lateinit var optionsBehavior: BottomSheetBehavior<LinearLayout>


    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 300L
        private const val PLAYLIST_ID = "playlist_id_long"
        fun createArgs(id: Long): Bundle = bundleOf(PLAYLIST_ID to id)
    }

    private val tracksList = ArrayList<Track>()
    private val tracksAdapter = TracksAdapter(tracksList,
        true,
        {track ->
        doOnClick(track)
        }, {track ->
        doOnLongClick(track)
    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getPlaylist(requireArguments().getLong(PLAYLIST_ID))

        optionsBehavior = BottomSheetBehavior.from(binding.options).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        var playlist: Playlist? = null

        binding.playlistsRV.layoutManager = LinearLayoutManager(requireContext())
        binding.playlistsRV.adapter = tracksAdapter

        viewModel.getPlaylistLiveData().observe(viewLifecycleOwner){ value ->
            when (value) {
                is PlaylistState.NoTracks -> {
                    bindPlaylistData(value.playlist, 0)
                    binding.bottomSheet.isVisible = false
                    playlist = value.playlist
                }
                is PlaylistState.Tracks -> {
                    var duration = 0L
                    for (track in value.tracks) {
                        duration += track.trackTimeMillis
                    }
                    bindPlaylistData(value.playlist, duration)
                    binding.bottomSheet.isVisible = true
                    bindTracks(value.tracks)
                    playlist = value.playlist
                }
                else -> {}
            }
        }

        binding.sharingBut.setOnClickListener {
            sharePlaylist(playlist!!)
        }

        optionsBehavior.addBottomSheetCallback(object: BottomSheetCallback(){
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.isVisible = false
                    }
                    else -> {
                        binding.overlay.isVisible = true
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) { }
        })

        binding.optionsBut.setOnClickListener {
            optionsBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.optionsShareBut.setOnClickListener {
            sharePlaylist(playlist!!)
        }

        binding.deletePlaylist.setOnClickListener {
            optionsBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            val confirmDialog = MaterialAlertDialogBuilder(requireContext())
                .setTitle("Хотите удалить плейлист <<${playlist?.name}>>?")
                .setNeutralButton(R.string.no) {_, _, ->}
                .setPositiveButton(R.string.yes) { _, _ ->
                    viewModel.deletePlaylist(playlist!!)
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            confirmDialog.show()
        }

        binding.redactInfo.setOnClickListener {
            if (playlist != null) {
                findNavController().navigate(
                    R.id.action_playlistFragment_to_playlistRedactorFragment,
                    PlaylistRedactorFragment.createArgs(playlist!!.id)
                )
            }
        }

        binding.buttonBack.setOnClickListener {
            if (requireActivity() is RootActivity) {
                (activity as RootActivity).showBottomNavigationView()
            }
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (requireActivity() is RootActivity) {
            (requireActivity() as RootActivity).showBottomNavigationView()
        }
    }

    private fun bindPlaylistData(playlist: Playlist, duration: Long) {
        binding.playlistName.text = playlist.name
        if (playlist.description.isNullOrEmpty()) {
            binding.playlistDescription.isVisible = false
        } else {
            binding.playlistDescription.isVisible = true
            binding.playlistDescription.text = playlist.description
        }
        binding.numberOfTracks.text = getPlaylistSizeText(playlist.size)
        binding.playlistDuration.text = getPlaylistDurationText(duration )
        Glide.with(this)
            .load(getPlaylistCoverUri(playlist))
            .centerCrop()
            .placeholder(R.drawable.big_placeholder_trackcover)
            .into(binding.playlistCover)
        binding.optionsPlaylistName.text = playlist.name
        binding.optionsPlaylistSize.text = getPlaylistSizeText(playlist.size)
        Glide.with(this)
            .load(getPlaylistCoverUri(playlist))
            .transform(CenterCrop(), RoundedCorners(dpToPx(2F, requireContext())))
            .placeholder(R.drawable.placeholder_trackcover)
            .into(binding.optionsPlaylistCover)
    }

    private fun bindTracks(tracks: List<Track>) {
        tracksList.clear()
        tracksList.addAll(tracks)
        binding.playlistsRV.adapter?.notifyDataSetChanged()
    }

    private fun doOnClick(track: Track) {
        val debounce = debounce(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) { track: Track ->
            viewModel.onTrackClick(track)
            findNavController().navigate(R.id.action_playlistFragment_to_audioplayerActivity)
        }
        debounce(track)
    }

    private fun doOnLongClick(track: Track): Boolean {
        val confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_track_title)
            .setMessage(R.string.delete_track_description)
            .setNeutralButton(R.string.no) {_, _, ->}
            .setPositiveButton(R.string.yes) { _, _ ->
                viewModel.deleteTrack(track)
            }
        confirmDialog.show()
        return true
    }

    private fun sharePlaylist(playlist: Playlist) {
        if (playlist?.size == 0) {
            Toast.makeText(requireContext(), "В этом плейлисте нет списка треков, которым можно поделиться", Toast.LENGTH_LONG).show()
        } else {
            var message = "${playlist.name}\n"
            if (playlist.description != "") {
                message += "${playlist.description}\n"
            }
            message += getPlaylistSizeText(playlist.size)
            for (track in tracksList) {
                message += "\n${tracksList.indexOf(track)+1}. ${track.artistName} - ${track.trackName} (${SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)})"
            }
            viewModel.sharePlaylist(message)
        }
    }

    private fun getPlaylistCoverUri(playlist: Playlist): Uri {
        val file = File(requireContext().getDir(NewPlaylistViewModel.DIR_NAME, Context.MODE_PRIVATE), playlist.coverPath)
        return file.toUri()
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

    private fun getPlaylistDurationText(duration: Long): String {
        var durationText = SimpleDateFormat("mm", Locale.getDefault()).format(duration)
        val dur = durationText.toInt()
        durationText += if (dur in 11..14) {
            " минут"
        } else if (dur %10 == 1) {
            " минута"
        } else if (dur %10 in 2..4) {
            " минуты"
        } else {
            " минут"
        }
        return durationText
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }
}