package com.example.playlistmaker.data.search.network

import com.example.playlistmaker.data.search.NetworkClient
import com.example.playlistmaker.data.search.dto.Response
import com.example.playlistmaker.data.search.dto.TracksSearchRequest

class RetrofitNetworkClient(
    private val itunesService: ITunesApi
) : NetworkClient {
    override fun doRequest(dto: Any): Response {
        if (dto is TracksSearchRequest) {
            try {
                val resp = itunesService.searchTracks(dto.searchRequest).execute()
                val body = resp.body() ?: Response()
                return body.apply { resultCode = resp.code() }
            } catch (e: Exception) {
                return Response().apply { resultCode = -1 }
            }
        } else {
            return Response().apply { resultCode = 400 }
        }
    }
}