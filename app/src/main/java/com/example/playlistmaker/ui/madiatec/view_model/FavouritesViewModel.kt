package com.example.playlistmaker.ui.madiatec.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.favourites.FavouritesInteractor
import com.example.playlistmaker.domain.player.ChosenTrackUseCase
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.launch

class FavouritesViewModel(
    private val favouritesInteractor: FavouritesInteractor,
    private val chosenTrackUseCase: ChosenTrackUseCase
): ViewModel() {

    init {
        getFavourites()
    }

    private val screenStateLiveData = MutableLiveData<FavouritesState>()

    fun getScreenStateLiveData(): LiveData<FavouritesState> = screenStateLiveData

    fun chooseTrack(track: Track) {
        chosenTrackUseCase.setTrack(track)
    }

    fun getFavourites() {
        viewModelScope.launch {
            favouritesInteractor.getFavourites().collect{ trackList ->
                if (trackList.isEmpty()){
                    screenStateLiveData.postValue(FavouritesState.StateEmpty)
                } else {
                    screenStateLiveData.postValue(FavouritesState.StateTracks(ArrayList(trackList)))
                }
            }
        }
    }
}