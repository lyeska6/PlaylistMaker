package com.example.playlistmaker.ui.madiatec.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlaylistsViewModel: ViewModel() {

    private val screenStateLiveData = MutableLiveData<PlaylistsState>(PlaylistsState.StateEmpty)

    fun getScreenStateLiveData(): LiveData<PlaylistsState> = screenStateLiveData
}