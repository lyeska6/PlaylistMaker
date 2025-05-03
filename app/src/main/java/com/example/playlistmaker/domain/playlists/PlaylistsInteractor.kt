package com.example.playlistmaker.domain.playlists

import com.example.playlistmaker.domain.playlists.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {

    suspend fun addTrackToAnyPlaylist(track: Track, playlist: Playlist)
    suspend fun addPlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist)
    fun getAllPlaylists(): Flow<List<Playlist>>
    fun getPlaylistById(id: Long): Flow<Playlist>
    fun getListOfTracksById(idList: List<String>): Flow<List<Track>>
    fun deleteTrackFromPlaylist(track: Track, playlist: Playlist): Flow<Playlist>
    fun setChosenTrack(track: Track)
    fun sharePlaylist(string: String)
}