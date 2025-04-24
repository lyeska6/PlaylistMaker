package com.example.playlistmaker.domain.playlists.model

import java.io.File

data class Playlist(
    val id: Long,
    val name: String,
    val description: String,
    val coverPath: String,
    val tracksIdList: List<String>,
    val size: Int
)
