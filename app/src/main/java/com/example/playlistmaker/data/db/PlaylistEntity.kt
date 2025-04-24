package com.example.playlistmaker.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
data class PlaylistEntity (
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "playlist_id")
    val id: Long,
    val name: String,
    val description: String,
    val coverPath: String,
    val tracksIdList: String,
    val size: Int
)