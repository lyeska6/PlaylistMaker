package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.PlaylistEntity
import com.example.playlistmaker.data.db.TrackEntity
import com.example.playlistmaker.data.db.TrackInAnyPlaylistEntity

@Dao
interface TrackInAnyPlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackInAnyPlaylistEntity)

    @Delete
    suspend fun deleteTrack(track: TrackInAnyPlaylistEntity)

    @Query("SELECT * FROM track_in_any_playlist_table")
    suspend fun getAllTracks(): List<TrackInAnyPlaylistEntity>
}