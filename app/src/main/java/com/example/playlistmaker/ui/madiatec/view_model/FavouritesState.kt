package com.example.playlistmaker.ui.madiatec.view_model

import com.example.playlistmaker.domain.search.model.Track

sealed class FavouritesState {
    data object StateEmpty: FavouritesState()
    data class StateTracks(
        val tracks: ArrayList<Track>
    ): FavouritesState()
}