package com.example.playlistmaker.ui.playlists.activity

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.playlists.model.Playlist
import com.example.playlistmaker.ui.playlists.view_model.NewPlaylistViewModel.Companion.DIR_NAME
import com.example.playlistmaker.ui.playlists.view_model.PlaylistRedactorViewModel
import com.example.playlistmaker.ui.playlists.view_model.PlaylistState
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class PlaylistRedactorFragment: NewPlaylistFragment() {

    companion object {
        private const val PLAYLIST_ID = "playlist_insert_id"

        fun createArgs(id: Long): Bundle = bundleOf(PLAYLIST_ID to id)
    }

    override val viewModel by viewModel<PlaylistRedactorViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getPlaylist(requireArguments().getLong(PLAYLIST_ID))

        binding.createNewPlaylistBut.text = getString(R.string.save)
        binding.heading.text = getString(R.string.redact)

        viewModel.getPlaylistLiveData().observe(viewLifecycleOwner) { playlistState ->
            when (playlistState) {
                is PlaylistState.NoTracks -> {
                    val playlist = playlistState.playlist
                    binding.nameEditText.text.insert(0, playlist.name)
                    binding.descriptionEditText.text.insert(0, playlist.description)
                    val coverUri = getPlaylistCoverUri(playlist)
                    Glide.with(requireContext())
                        .load(coverUri)
                        .transform(
                            CenterCrop(),
                            RoundedCorners(dpToPx(8F, requireContext()))
                        )
                        .placeholder(requireContext().getDrawable(R.drawable.playlist_cover_empty))
                        .into(binding.playlistCoverView)
                    isCoverLoaded = coverUri
                }
                else -> {}
            }
        }
    }

    override fun goBack() {
        reallyGoBack()
    }

    override fun reallyGoBack() {
        backCallback.isEnabled = false
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    override fun savePlaylist() {
        when (val value = viewModel.getPlaylistLiveData().value) {
            is PlaylistState.NoTracks -> {
                val playlist = value.playlist
                if (playlist.name != binding.nameEditText.text.toString()
                    || playlist.description != binding.descriptionEditText.text.toString()
                    || isCoverLoaded != getPlaylistCoverUri(playlist)) {
                    var coverPath = playlist.coverPath
                    if (isCoverLoaded != null && isCoverLoaded != getPlaylistCoverUri(playlist)) {
                        coverPath = saveImageToPrivateStorage(binding.nameEditText.text.toString()+playlist.id.toString(), isCoverLoaded!!)
                    }
                    viewModel.updatePlaylist(
                        Playlist(
                            playlist.id,
                            binding.nameEditText.text.toString(),
                            binding.descriptionEditText.text.toString(),
                            coverPath,
                            playlist.tracksIdList,
                            playlist.size
                        )
                    )
                }
                reallyGoBack()
            }
            else -> {}
        }

    }

    private fun getPlaylistCoverUri(playlist: Playlist): Uri {
        val file = File(requireContext().getDir(DIR_NAME, Context.MODE_PRIVATE), playlist.coverPath)
        return file.toUri()
    }
}