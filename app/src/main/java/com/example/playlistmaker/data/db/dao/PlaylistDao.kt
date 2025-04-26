package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.playlistmaker.data.db.PlaylistEntity
import com.example.playlistmaker.data.db.TrackEntity

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Delete
    suspend fun deletePlaylist(playlist: PlaylistEntity)

    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlist_table")
    suspend fun getPlaylistsList(): List<PlaylistEntity>

    @Query("SELECT * FROM playlist_table WHERE playlist_id = :playlistId")
    suspend fun getPlaylistById(playlistId: Long): PlaylistEntity
}