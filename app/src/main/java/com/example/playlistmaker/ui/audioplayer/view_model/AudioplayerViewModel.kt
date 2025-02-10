package com.example.playlistmaker.ui.audioplayer.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.player.AudioplayerInteractor
import com.example.playlistmaker.domain.player.GetChosenTrackUseCase
import com.example.playlistmaker.domain.search.model.Track

class AudioplayerViewModel(
    getChosenTrackUseCase: GetChosenTrackUseCase,
    private val audioplayerInteractor: AudioplayerInteractor
): ViewModel() {

    private var mainHandler: Handler = Handler(Looper.getMainLooper())
    private var setTimingRunnable = Runnable {setCurrentTiming()}

    private val trackLiveData = MutableLiveData(getChosenTrackUseCase.getTrack())
    private val playerStateLiveData = MutableLiveData<PlayerState>(PlayerState.StateDefault)

    fun getTrackLiveData(): LiveData<Track> = trackLiveData
    fun getPlayerStateLiveData(): LiveData<PlayerState> = playerStateLiveData

    private val prepareConsumer = object: AudioplayerInteractor.Consumer {
        override fun consume() {
            mainHandler.removeCallbacks(setTimingRunnable)
            playerStateLiveData.postValue(PlayerState.StatePrepared)
        }
    }

    fun preparePlayer(track: Track) {
        audioplayerInteractor.preparePlayer(
            track,
            prepareConsumer,
            prepareConsumer
        )
    }

    private fun setTimingState() {
        if (playerStateLiveData.value is PlayerState.StatePlaying) {
            playerStateLiveData.postValue(PlayerState.StatePlaying(audioplayerInteractor.getCurrentTiming()))
        }
    }
    private fun setCurrentTiming() {
        if (playerStateLiveData.value is PlayerState.StatePlaying) {
            setTimingState()
            mainHandler.postDelayed(setTimingRunnable, 300L)
        }
    }

    fun pausePlayer() {
        mainHandler.removeCallbacks(setTimingRunnable)
        playerStateLiveData.postValue(PlayerState.StatePaused)
        audioplayerInteractor.pausePlayer()
    }

    private fun startPreparedPlayer() {
        audioplayerInteractor.startPlayer()
        playerStateLiveData.postValue(PlayerState.StatePlaying(0))
        mainHandler.postDelayed(setTimingRunnable, 300L)
    }

    private fun startPausedPlayer() {
        audioplayerInteractor.startPlayer()
        playerStateLiveData.postValue(PlayerState.StatePlaying(audioplayerInteractor.getCurrentTiming()))
        mainHandler.postDelayed(setTimingRunnable, 300L)
    }

    fun startOrPausePlayer() {
        when (playerStateLiveData.value) {
            PlayerState.StateDefault,
            null -> {}
            PlayerState.StatePrepared -> {
                startPreparedPlayer()
            }
            PlayerState.StatePaused -> {
                startPausedPlayer()
            }
            is PlayerState.StatePlaying -> {
                pausePlayer()
            }
        }
    }

    fun onDestroy() {
        audioplayerInteractor.releasePlayer()
        mainHandler.removeCallbacks(setTimingRunnable)
    }
}