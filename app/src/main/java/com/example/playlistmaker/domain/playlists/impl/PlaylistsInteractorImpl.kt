package com.example.playlistmaker.domain.playlists.impl

import com.example.playlistmaker.domain.playlists.PlaylistsInteractor
import com.example.playlistmaker.domain.playlists.PlaylistsRepository
import com.example.playlistmaker.domain.playlists.model.Playlist
import com.example.playlistmaker.domain.search.SearchHistoryRepository
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.domain.sharing.ExternalNavigator
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(
    private val repository: PlaylistsRepository,
    private val trackRepository: SearchHistoryRepository,
    private val externalNavigator: ExternalNavigator
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

    override suspend fun updatePlaylist(playlist: Playlist) {
        repository.updatePlaylist(playlist)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> = repository.getPlaylistsList()

    override fun getPlaylistById(id: Long): Flow<Playlist> = repository.getPlaylistById(id)

    override fun getListOfTracksById(idList: List<String>): Flow<List<Track>> = repository.getListOfTracksById(idList)

    override fun deleteTrackFromPlaylist(track: Track, playlist: Playlist): Flow<Playlist> = repository.deleteTrackFromPlaylist(track, playlist)

    override fun setChosenTrack(track: Track) {
        trackRepository.setChosenTrack(track)
    }

    override fun sharePlaylist(string: String) {
        externalNavigator.shareString(string)
    }
}