package com.example.playlistmaker.data.converters

import com.example.playlistmaker.data.db.TrackInAnyPlaylistEntity
import com.example.playlistmaker.domain.search.model.Track

class TrackInAnyPlaylistConvertor {

    fun map(track: Track): TrackInAnyPlaylistEntity {
        return TrackInAnyPlaylistEntity(track.trackId, track.trackName, track.artistName, track.trackTimeMillis, track.artworkUrl100, track.collectionName, track.releaseDate, track.primaryGenreName, track.country, track.previewUrl)
    }

    fun map(track: TrackInAnyPlaylistEntity): Track {
        return Track(track.trackName, track.artistName, track.trackTimeMillis, track.artworkUrl100, track.trackId, track.collectionName, track.releaseDate, track.primaryGenreName, track.country, track.previewUrl)
    }
}