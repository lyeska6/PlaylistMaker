package com.example.playlistmaker.ui.madiatec.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentFavouriteTracksBinding
import com.example.playlistmaker.ui.madiatec.view_model.FavouritesState
import com.example.playlistmaker.ui.madiatec.view_model.FavouritesViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavouritesFragment: Fragment() {

    companion object {
        fun newInstance(): FavouritesFragment = FavouritesFragment()
    }

    private lateinit var binding: FragmentFavouriteTracksBinding
    private val viewModel by viewModel<FavouritesViewModel>()

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

                }
                is FavouritesState.StateTracks -> {

                }
            }
        }
    }
}