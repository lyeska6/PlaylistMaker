package com.example.playlistmaker.ui.madiatec.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavouriteTracksBinding
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.madiatec.view_model.FavouritesState
import com.example.playlistmaker.ui.madiatec.view_model.FavouritesViewModel
import com.example.playlistmaker.ui.search.activity.TracksAdapter
import com.example.playlistmaker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavouritesFragment: Fragment() {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 300L

        fun newInstance(): FavouritesFragment = FavouritesFragment()
    }

    private lateinit var binding: FragmentFavouriteTracksBinding
    private val viewModel by viewModel<FavouritesViewModel>()

    private lateinit var onTrackClickDebounce: (Track) -> Unit
    private val tracksArrayList = ArrayList<Track>()
    private val tracksAdapter = TracksAdapter(
        tracksArrayList
    ) { track ->
        onTrackClickDebounce(track)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentFavouriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getScreenStateLiveData().observe(viewLifecycleOwner){ state ->
            when (state) {
                FavouritesState.StateEmpty -> {
                    renderEmpty()
                }
                is FavouritesState.StateTracks -> {
                    renderTracks(state.tracks)
                }
            }
        }

        binding.tracksRV.layoutManager = LinearLayoutManager(requireContext())
        binding.tracksRV.adapter = tracksAdapter
        onTrackClickDebounce = debounce(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) { track ->
            viewModel.chooseTrack(track)
            findNavController().navigate(R.id.action_mediatecFragment_to_audioplayerActivity)
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.getFavourites()
    }

    private fun renderEmpty() {
        binding.emptyImage.isVisible = true
        binding.emptyText.isVisible = true
        binding.tracksRV.isVisible = false
    }

    private fun renderTracks(tracks: ArrayList<Track>){
        binding.emptyImage.isVisible = false
        binding.emptyText.isVisible = false
        binding.tracksRV.isVisible = true

        tracksArrayList.clear()
        tracksArrayList.addAll(tracks)
        tracksAdapter.notifyDataSetChanged()
    }
}