package com.example.playlistmaker.data

import android.util.Log
import com.example.playlistmaker.data.converters.PlaylistDbConvertor
import com.example.playlistmaker.data.converters.TrackDbConvertor
import com.example.playlistmaker.data.converters.TrackInAnyPlaylistConvertor
import com.example.playlistmaker.data.db.AppDataBase
import com.example.playlistmaker.domain.playlists.PlaylistsRepository
import com.example.playlistmaker.domain.playlists.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistsRepositoryImpl(
    private val playlistConvertor: PlaylistDbConvertor,
    private val trackConvertor: TrackInAnyPlaylistConvertor,
    private val dataBase: AppDataBase
): PlaylistsRepository {

    override suspend fun addTrackToAnyPlaylist(track: Track, playlist: Playlist) {
        dataBase.getTrackInAnyPlaylistDao().insertTrack(trackConvertor.map(track))
        val newIdList = playlist.tracksIdList + track.trackId
        val newPlaylist = Playlist(playlist.id, playlist.name, playlist.description, playlist.coverPath, newIdList, playlist.size+1)
        updatePlaylist(newPlaylist)
    }

    override suspend fun addPlaylist(playlist: Playlist) {
        val playlistEntity = playlistConvertor.map(playlist)
        dataBase.getPlaylistDao().insertPlaylist(playlistEntity)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        dataBase.getPlaylistDao().deletePlaylist(playlistConvertor.map(playlist))
        getListOfTracksById(playlist.tracksIdList).collect{ tracks ->
            for (track in tracks) {
                deleteTrackFromAnyPlaylist(track)
            }
        }
    }

    override fun getPlaylistsList(): Flow<List<Playlist>> = flow {
        val list = dataBase.getPlaylistDao().getPlaylistsList().map { playlistEntity -> playlistConvertor.map(playlistEntity) }
        emit(list)
    }

    override fun getPlaylistById(id: Long): Flow<Playlist> = flow {
        val playlistEntity = dataBase.getPlaylistDao().getPlaylistById(id)
        val playlist = playlistConvertor.map(playlistEntity)
        emit(playlist)
    }

    override fun getListOfTracksById(idList: List<String>): Flow<List<Track>> = flow {
        var trackList = emptyList<Track>()
        val trackEntityList = dataBase.getTrackInAnyPlaylistDao().getAllTracks()
        for (trackE in trackEntityList) {
            if (trackE.trackId in idList) {
                val track = trackConvertor.map(trackE)
                trackList = trackList + track
            }
        }
        emit(trackList)
    }

    override fun deleteTrackFromPlaylist(track: Track, playlist: Playlist): Flow<Playlist> = flow {
        val newIdList = arrayListOf<String>().apply {
            addAll(playlist.tracksIdList)
            remove(track.trackId)
        }
        val newPlaylist = Playlist(playlist.id, playlist.name, playlist.description, playlist.coverPath, newIdList.toList(), playlist.size-1)
        updatePlaylist(newPlaylist)
        deleteTrackFromAnyPlaylist(track)
        emit(newPlaylist)
    }

    private suspend fun deleteTrackFromAnyPlaylist(track: Track) {
        val allTracksIdLists = dataBase.getPlaylistDao().getAllIdLists()
        var needToDelete = true
        for (list in allTracksIdLists) {
            if (track.trackId in playlistConvertor.jsonToList(list)) {
                needToDelete = false
                break
            }
        }
        if (needToDelete) {
            dataBase.getTrackInAnyPlaylistDao().deleteTrack(trackConvertor.map(track))
        }
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        dataBase.getPlaylistDao().updatePlaylist(playlistConvertor.map(playlist))
    }
}