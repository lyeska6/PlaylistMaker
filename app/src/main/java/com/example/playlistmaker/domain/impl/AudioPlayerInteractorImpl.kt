package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.Creator
import com.example.playlistmaker.domain.api.AudioPlayerInteractor
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track

class AudioPlayerInteractorImpl(
    val repository: SearchHistoryRepository
) : AudioPlayerInteractor {

    private val player = Creator.getMediaPlayer()
    private var playerState = STATE_DEFAULT

    override fun getTrack(): Track {
        return repository.getChosenTrack()
    }

    override fun preparePlayer(
        track: Track,
        preparedConsumer: AudioPlayerInteractor.Consumer,
        completionConsumer: AudioPlayerInteractor.Consumer
    ) {
        player.setDataSource(track.previewUrl)
        player.prepareAsync()
        player.setOnPreparedListener {
            playerState = STATE_PREPARED
            preparedConsumer.consume()
        }
        player.setOnCompletionListener {
            playerState = STATE_PREPARED
            completionConsumer.consume()
        }
    }

    override fun getCurrentTiming(): Int {
        return player.currentPosition
    }

    override fun releasePlayer() {
        player.release()
    }

    override fun startOrPausePlayer(
        pauseConsumer: AudioPlayerInteractor.Consumer,
        startConsumer: AudioPlayerInteractor.Consumer
    ) {
        when (playerState) {
            STATE_PLAYING -> pausePlayer(pauseConsumer)
            STATE_PREPARED,
            STATE_PAUSED -> startPlayer(startConsumer)
        }
    }

    private fun startPlayer(consumer: AudioPlayerInteractor.Consumer) {
        player.start()
        playerState = STATE_PLAYING
        consumer.consume()
    }

    override fun pausePlayer(pauseConsumer: AudioPlayerInteractor.Consumer) {
        player.pause()
        playerState = STATE_PAUSED
        pauseConsumer.consume()
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }
}