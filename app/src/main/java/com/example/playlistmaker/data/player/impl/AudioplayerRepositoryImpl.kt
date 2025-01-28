package com.example.playlistmaker.data.player.impl

import android.media.MediaPlayer
import com.example.playlistmaker.domain.player.AudioplayerInteractor
import com.example.playlistmaker.domain.player.AudioplayerRepository
import com.example.playlistmaker.domain.search.model.Track

class AudioplayerRepositoryImpl(
    private val player: MediaPlayer
): AudioplayerRepository {

    override fun preparePlayer(
        track: Track,
        preparedConsumer: AudioplayerInteractor.Consumer,
        completionConsumer: AudioplayerInteractor.Consumer
    ) {
        player.setDataSource(track.previewUrl)
        player.prepareAsync()
        player.setOnPreparedListener {
            preparedConsumer.consume()
        }
        player.setOnCompletionListener {
            completionConsumer.consume()
        }
    }

    override fun getCurrentTiming(): Int {
        return player.currentPosition
    }

    override fun releasePlayer() {
        player.release()
    }

    override fun startPlayer() {
        player.start()
    }

    override fun pausePlayer() {
        player.pause()
    }
}