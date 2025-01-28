package com.example.playlistmaker.domain.player

import com.example.playlistmaker.domain.search.model.Track

interface AudioplayerRepository {

    fun preparePlayer(track: Track,
                      preparedConsumer: AudioplayerInteractor.Consumer,
                      completionConsumer: AudioplayerInteractor.Consumer)

    fun getCurrentTiming(): Int

    fun releasePlayer()

    fun startPlayer()

    fun pausePlayer()
}