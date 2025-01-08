package com.example.playlistmaker.domain.impl

import android.media.MediaPlayer
import com.example.playlistmaker.domain.api.AudioPlayerInteractor
import com.example.playlistmaker.domain.api.SharedPrefsRepository
import com.example.playlistmaker.domain.models.Track

class AudioPlayerInteractorImpl(
    val repository: SharedPrefsRepository
) : AudioPlayerInteractor {

    private val player = MediaPlayer()
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

    override fun startPlayer(consumer: AudioPlayerInteractor.Consumer) {
        player.start()
        playerState = STATE_PLAYING
        consumer.consume()
    }

    override fun pausePlayer(consumer: AudioPlayerInteractor.Consumer) {
        player.pause()
        playerState = STATE_PAUSED
        consumer.consume()
    }

    override fun getCurrentTiming(): Int {
        return player.currentPosition
    }

    override fun releasePlayer() {
        player.release()
    }

    override fun clickPlayButton(
        pauseConsumer: AudioPlayerInteractor.Consumer,
        startConsumer: AudioPlayerInteractor.Consumer
    ) {
        when (playerState) {
            STATE_PLAYING -> pausePlayer(pauseConsumer)
            STATE_PREPARED,
            STATE_PAUSED -> startPlayer(startConsumer)
        }
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }
}