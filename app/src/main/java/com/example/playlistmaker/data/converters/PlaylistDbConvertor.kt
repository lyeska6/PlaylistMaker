package com.example.playlistmaker.data.converters

import com.example.playlistmaker.data.db.PlaylistEntity
import com.example.playlistmaker.domain.playlists.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import com.google.gson.Gson

class PlaylistDbConvertor(private val gson: Gson) {

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(playlist.id, playlist.name, playlist.description, playlist.coverPath, listToJson(playlist.tracksIdList), playlist.size)
    }

    fun map(playlistEntity: PlaylistEntity): Playlist {
        return Playlist(playlistEntity.id, playlistEntity.name, playlistEntity.description, playlistEntity.coverPath, jsonToList(playlistEntity.tracksIdList), playlistEntity.size)
    }


    private fun listToJson(list: List<String>): String {
        return gson.toJson(list.toTypedArray())
    }

    fun jsonToList(json: String): List<String> {
        return gson.fromJson(
                json,
                Array<String>::class.java
            ).asList()
    }
}