package com.example.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.data.db.dao.PlaylistDao
import com.example.playlistmaker.data.db.dao.TrackDao
import com.example.playlistmaker.data.db.dao.TrackInAnyPlaylistDao

@Database(
    version = 5,
    entities = [
        TrackEntity::class,
        PlaylistEntity::class,
        TrackInAnyPlaylistEntity::class
    ]
)
abstract class AppDataBase: RoomDatabase() {

    abstract fun getTrackDao(): TrackDao
    abstract fun getPlaylistDao(): PlaylistDao
    abstract fun getTrackInAnyPlaylistDao(): TrackInAnyPlaylistDao
}