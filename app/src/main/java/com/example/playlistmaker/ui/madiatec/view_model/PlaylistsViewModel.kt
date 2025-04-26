package com.example.playlistmaker.ui.madiatec.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.playlists.PlaylistsInteractor
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val interactor: PlaylistsInteractor
): ViewModel() {

    init {
        getPlaylists()
    }

    private val screenStateLiveData = MutableLiveData<PlaylistsState>(PlaylistsState.StateEmpty)

    fun getScreenStateLiveData(): LiveData<PlaylistsState> = screenStateLiveData

    fun getPlaylists() {
        viewModelScope.launch {
            interactor.getAllPlaylists().collect{ playlists ->
                if (!playlists.isNullOrEmpty()) {
                    screenStateLiveData.postValue(PlaylistsState.StateData(playlists))
                } else {
                    screenStateLiveData.postValue(PlaylistsState.StateEmpty)
                }
            }
        }
    }
}