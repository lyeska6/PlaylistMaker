package com.example.playlistmaker.ui.playlists.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.domain.playlists.model.Playlist
import com.example.playlistmaker.ui.playlists.view_model.PlaylistsState
import com.example.playlistmaker.ui.playlists.view_model.PlaylistsViewModel
import com.example.playlistmaker.ui.root.RootActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment: Fragment() {

    companion object{
        fun newInstance(): PlaylistsFragment = PlaylistsFragment()
    }

    private val viewModel by viewModel<PlaylistsViewModel>()
    private lateinit var binding: FragmentPlaylistsBinding

    private var playlists = ArrayList<Playlist>()
    private val adapter = PlaylistsAdapter(playlists) { id ->
        goToThePlaylist(id)
    }

    override fun onStart() {
        super.onStart()
        viewModel.getPlaylists()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getScreenStateLiveData().observe(viewLifecycleOwner){ state ->
            when (state) {
                PlaylistsState.StateEmpty -> {
                    viewEmpty()
                }
                is PlaylistsState.StateData -> {
                    viewData(state.playlists)
                }
            }
        }

        binding.newPlaylistBut.setOnClickListener {
            (activity as RootActivity).hideBottomNavigationView()
            findNavController().navigate(R.id.action_mediatecFragment_to_newPlaylistFragment)
        }

        binding.playlistsRV.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.playlistsRV.adapter = adapter
    }

    private fun viewEmpty() {
        binding.emptyImage.isVisible = true
        binding.emptyText.isVisible = true

        binding.playlistsRV.isVisible = false
    }

    private fun viewData(newPlaylists: List<Playlist>) {
        binding.emptyImage.isVisible = false
        binding.emptyText.isVisible = false

        playlists.clear()
        playlists.addAll(newPlaylists)
        binding.playlistsRV.adapter?.notifyDataSetChanged()
        binding.playlistsRV.isVisible = true
    }

    private fun goToThePlaylist(id: Long) {
        (activity as RootActivity).hideBottomNavigationView()
        findNavController().navigate(R.id.action_mediatecFragment_to_playlistFragment, PlaylistFragment.createArgs(id))
    }

}