package com.example.playlistmaker.ui.audioplayerPage.view_model

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.player.AudioPlayerInteractor
import com.example.playlistmaker.domain.search.model.Track

class AudioplayerViewModel(
    audioPlayerInteractor: AudioPlayerInteractor
) : ViewModel() {

    private val trackLiveData = MutableLiveData(audioPlayerInteractor.getTrack())

    fun getTrackLiveData(): LiveData<Track> = trackLiveData

    private val player = MediaPlayer()

    private val playerStateLiveData = MutableLiveData<PlayerState>(PlayerState.StateDefault)

    fun getPlayerStateLiveData(): LiveData<PlayerState> = playerStateLiveData

    fun preparePlayer() {
        player.setDataSource(trackLiveData.value?.previewUrl)
        player.prepareAsync()
        player.setOnPreparedListener {
            playerStateLiveData.postValue(PlayerState.StatePrepared)
        }
        player.setOnCompletionListener {
            playerStateLiveData.postValue(PlayerState.StatePrepared)
        }
    }

    private val currentTimingLiveData = MutableLiveData(0)

    fun getCurrentTimingLiveData(): LiveData<Int> = currentTimingLiveData

    fun setCurrentTiming() {
        if (playerStateLiveData.value is PlayerState.StatePrepared || playerStateLiveData.value is PlayerState.StateDefault) {
            currentTimingLiveData.postValue(0)
        } else {
            currentTimingLiveData.postValue(player.currentPosition)
        }
    }

    fun pausePlayer() {
        player.pause()
        playerStateLiveData.postValue(PlayerState.StatePaused)
    }

    private fun startPlayer() {
        player.start()
        playerStateLiveData.postValue(PlayerState.StatePlaying)
    }

    fun startOrPausePlayer() {
        when (playerStateLiveData.value) {
            PlayerState.StateDefault,
            null -> TODO()
            PlayerState.StatePrepared,
            PlayerState.StatePaused -> {
                startPlayer()
            }
            PlayerState.StatePlaying -> {
                pausePlayer()
            }
        }
    }

    fun releasePlayer() {
        player.release()
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AudioplayerViewModel(
                    Creator.provideAudioPlayerInteractor()
                )
            }
        }
    }
}