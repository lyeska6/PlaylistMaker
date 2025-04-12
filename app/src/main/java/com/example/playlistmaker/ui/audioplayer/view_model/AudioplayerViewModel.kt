package com.example.playlistmaker.ui.audioplayer.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.player.AudioplayerInteractor
import com.example.playlistmaker.domain.player.GetChosenTrackUseCase
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.utils.debounce
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class AudioplayerViewModel(
    getChosenTrackUseCase: GetChosenTrackUseCase,
    private val audioplayerInteractor: AudioplayerInteractor
): ViewModel() {

    private var timerJob: Job? = null

    private val trackLiveData = MutableLiveData(getChosenTrackUseCase.getTrack())
    private val playerStateLiveData = MutableLiveData<PlayerState>(PlayerState.StateDefault())

    fun getTrackLiveData(): LiveData<Track> = trackLiveData
    fun getPlayerStateLiveData(): LiveData<PlayerState> = playerStateLiveData

    private val prepareConsumer = object: AudioplayerInteractor.Consumer {
        override fun consume() {
            playerStateLiveData.postValue(PlayerState.StatePrepared())
        }
    }

    fun preparePlayer(track: Track) {
        audioplayerInteractor.preparePlayer(
            track,
            prepareConsumer,
            prepareConsumer
        )
    }

    fun pausePlayer() {
        audioplayerInteractor.pausePlayer()
        timerJob?.cancel()
        playerStateLiveData.postValue(PlayerState.StatePaused(getTiming()))
    }

    private fun startPlayer(wasPrepared: Boolean) {
        audioplayerInteractor.startPlayer()
        val time = if (wasPrepared) ZERO_TIME else getTiming()
        playerStateLiveData.postValue(PlayerState.StatePlaying(time))
        startTimer()
    }

    fun startOrPausePlayer() {
        when (playerStateLiveData.value) {
            is PlayerState.StateDefault,
            null -> {}
            is PlayerState.StatePrepared -> {
                startPlayer(true)
            }
            is PlayerState.StatePaused -> {
                startPlayer(false)
            }
            is PlayerState.StatePlaying -> {
                pausePlayer()
            }
        }
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            delay(GET_TIMING_DELAY)
            while (playerStateLiveData.value is PlayerState.StatePlaying){
                playerStateLiveData.postValue(PlayerState.StatePlaying(getTiming()))
                delay(GET_TIMING_DELAY)
            }
        }
    }

    fun onDestroy() {
        audioplayerInteractor.releasePlayer()
    }

    private fun getTiming(): String = formatTiming(audioplayerInteractor.getCurrentTiming())
    private fun formatTiming(time: Int): String = SimpleDateFormat("mm:ss", Locale.getDefault()).format(time)

    companion object {
        const val GET_TIMING_DELAY = 300L
        const val ZERO_TIME = "00:00"
    }
}