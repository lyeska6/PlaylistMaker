package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface AudioPlayerInteractor {

    fun getTrack(): Track

    fun preparePlayer(track: Track, preparedConsumer: Consumer, completionConsumer: Consumer)

    fun getCurrentTiming(): Int

    fun releasePlayer()

    fun startOrPausePlayer(pauseConsumer: Consumer, startConsumer: Consumer)

    fun pausePlayer(pauseConsumer: Consumer)

    interface Consumer {
        fun consume()
    }
}