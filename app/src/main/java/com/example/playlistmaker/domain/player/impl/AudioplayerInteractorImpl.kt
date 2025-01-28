package com.example.playlistmaker.domain.player.impl

import com.example.playlistmaker.domain.player.AudioplayerInteractor
import com.example.playlistmaker.domain.player.AudioplayerRepository
import com.example.playlistmaker.domain.search.model.Track

class AudioplayerInteractorImpl(
    private val repository: AudioplayerRepository
): AudioplayerInteractor {

    override fun preparePlayer(
        track: Track,
        preparedConsumer: AudioplayerInteractor.Consumer,
        completionConsumer: AudioplayerInteractor.Consumer
    ) {
        repository.preparePlayer(
            track,
            preparedConsumer,
            completionConsumer
        )
    }

    override fun getCurrentTiming(): Int {
        return repository.getCurrentTiming()
    }

    override fun releasePlayer() {
        repository.releasePlayer()
    }

    override fun startPlayer() {
        repository.startPlayer()
    }

    override fun pausePlayer() {
        repository.pausePlayer()
    }
}