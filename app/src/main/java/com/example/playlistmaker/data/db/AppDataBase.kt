package com.example.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.data.db.dao.TrackDao

@Database(
    version = 1,
    entities = [
        TrackEntity::class
    ]
)
abstract class AppDataBase: RoomDatabase() {

    abstract fun getTrackDao(): TrackDao
}