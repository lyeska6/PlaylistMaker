package com.example.playlistmaker.data.converters

import com.example.playlistmaker.data.db.TrackEntity
import com.example.playlistmaker.domain.search.model.Track

class TrackDbConvertor {

    fun map(track: Track): TrackEntity{
        return TrackEntity(track.trackId, track.trackName, track.artistName, track.trackTimeMillis, track.artworkUrl100, track.collectionName, track.releaseDate, track.primaryGenreName, track.country, track.previewUrl)
    }

    fun map(track: TrackEntity): Track{
        return Track(track.trackName, track.artistName, track.trackTimeMillis, track.artworkUrl100, track.trackId, track.collectionName, track.releaseDate, track.primaryGenreName, track.country, track.previewUrl)
    }
}