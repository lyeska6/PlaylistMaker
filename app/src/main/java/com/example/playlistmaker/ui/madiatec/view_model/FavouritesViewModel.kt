package com.example.playlistmaker.ui.madiatec.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FavouritesViewModel: ViewModel() {

    private val screenStateLiveData = MutableLiveData<FavouritesState>(FavouritesState.StateEmpty)

    fun getScreenStateLiveData(): LiveData<FavouritesState> = screenStateLiveData

}