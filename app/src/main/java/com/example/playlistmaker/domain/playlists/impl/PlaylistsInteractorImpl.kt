package com.example.playlistmaker.domain.playlists.impl

import com.example.playlistmaker.domain.playlists.PlaylistsInteractor
import com.example.playlistmaker.domain.playlists.PlaylistsRepository
import com.example.playlistmaker.domain.playlists.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(
    private val repository: PlaylistsRepository
): PlaylistsInteractor {

    override suspend fun addTrackToAnyPlaylist(track: Track, playlist: Playlist) {
        repository.addTrackToAnyPlaylist(track, playlist)
    }
    override suspend fun addPlaylist(playlist: Playlist) {
        repository.addPlaylist(playlist)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        repository.deletePlaylist(playlist)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> = repository.getPlaylistsList()

    override fun getPlaylistById(id: Long): Flow<Playlist> = repository.getPlaylistById(id)

}