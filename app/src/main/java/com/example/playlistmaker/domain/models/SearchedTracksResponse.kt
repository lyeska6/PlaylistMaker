package com.example.playlistmaker.domain.models

class SearchedTracksResponse(
    val resultCode: Int
) {
    var trackList = emptyList<Track>()
}