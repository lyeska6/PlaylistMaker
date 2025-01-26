package com.example.playlistmaker.domain.search.model

class SearchedTracksResponse(
    val resultCode: Int
) {
    var trackList = emptyList<Track>()
}