package com.example.playlistmaker.data.search.network

import com.example.playlistmaker.data.search.dto.TracksSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApi {
    @GET("/search?entity=song ")
    suspend fun searchTracks(
        @Query("term", encoded = false) text: String
    ): TracksSearchResponse
}