package com.example.playlistmaker.data

import com.example.playlistmaker.data.dto.ChangeThemeRequest
import com.example.playlistmaker.data.dto.ChangeThemeResponse
import com.example.playlistmaker.data.dto.GetChosenTrackRequest
import com.example.playlistmaker.data.dto.GetChosenTrackResponse
import com.example.playlistmaker.data.dto.GetSearchHistoryRequest
import com.example.playlistmaker.data.dto.GetSearchHistoryResponse
import com.example.playlistmaker.data.dto.GetThemeRequest
import com.example.playlistmaker.data.dto.GetThemeResponse
import com.example.playlistmaker.data.dto.SetChosenTrackRequest
import com.example.playlistmaker.data.dto.SetSearchHistoryRequest
import com.example.playlistmaker.data.dto.SetSearchHistoryResponse
import com.example.playlistmaker.domain.api.SharedPrefsRepository
import com.example.playlistmaker.domain.models.DarkThemeState
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson

class SharedPrefsRepositoryImpl(val networkClient: NetworkClient) : SharedPrefsRepository {

    override fun changeTheme(darkThemeEnabled: Boolean): DarkThemeState {
        val response = networkClient.doRequest(ChangeThemeRequest(darkThemeEnabled))
        return DarkThemeState((response as ChangeThemeResponse).result.currentState)
    }

    override fun getTheme(): DarkThemeState {
        val response = networkClient.doRequest(GetThemeRequest())
        return DarkThemeState((response as GetThemeResponse).result.currentState)
    }

    override fun getSearchHistory(elseString: String): String {
        val response = networkClient.doRequest(GetSearchHistoryRequest(elseString))
        return (response as GetSearchHistoryResponse).result
    }

    override fun setSearchHistory(jsonList: String): String {
        val response = networkClient.doRequest(SetSearchHistoryRequest(jsonList))
        return (response as SetSearchHistoryResponse).result
    }

    override fun setChosenTrack(jsonTrack: String) {
        networkClient.doRequest(SetChosenTrackRequest(jsonTrack))
    }

    override fun getChosenTrack(): Track {
        val jsonTrack =
            (networkClient.doRequest(GetChosenTrackRequest()) as GetChosenTrackResponse).jsonTrack
        return Gson().fromJson(jsonTrack, Track::class.java)
    }
}