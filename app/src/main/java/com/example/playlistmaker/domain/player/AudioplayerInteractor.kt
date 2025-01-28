package com.example.playlistmaker.domain.player

import com.example.playlistmaker.domain.search.model.Track

interface AudioplayerInteractor {

    fun preparePlayer(track: Track, preparedConsumer: Consumer, completionConsumer: Consumer)

    fun getCurrentTiming(): Int

    fun releasePlayer()

    fun startPlayer()

    fun pausePlayer()

    interface Consumer {
        fun consume()
    }
}