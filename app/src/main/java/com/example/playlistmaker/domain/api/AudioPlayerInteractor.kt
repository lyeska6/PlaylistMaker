package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface AudioPlayerInteractor {

    fun getTrack(): Track

    fun preparePlayer(track: Track, preparedConsumer: Consumer, completionConsumer: Consumer)

    fun startPlayer(consumer: Consumer)

    fun pausePlayer(consumer: Consumer)

    fun getCurrentTiming(): Int

    fun releasePlayer()

    fun clickPlayButton(pauseConsumer: Consumer, startConsumer: Consumer)

    interface Consumer {
        fun consume()
    }
}