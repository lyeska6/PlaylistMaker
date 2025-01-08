package com.example.playlistmaker.data.network

import com.example.playlistmaker.Creator
import com.example.playlistmaker.KEY_CHOSEN_TRACK
import com.example.playlistmaker.KEY_SEARCH_HISTORY_LIST
import com.example.playlistmaker.THEME_PREFERENCE_KEY
import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.ChangeThemeRequest
import com.example.playlistmaker.data.dto.ChangeThemeResponse
import com.example.playlistmaker.data.dto.DarkThemeStateDto
import com.example.playlistmaker.data.dto.GetChosenTrackRequest
import com.example.playlistmaker.data.dto.GetChosenTrackResponse
import com.example.playlistmaker.data.dto.GetSearchHistoryRequest
import com.example.playlistmaker.data.dto.GetSearchHistoryResponse
import com.example.playlistmaker.data.dto.GetThemeRequest
import com.example.playlistmaker.data.dto.GetThemeResponse
import com.example.playlistmaker.data.dto.Response
import com.example.playlistmaker.data.dto.SetChosenTrackRequest
import com.example.playlistmaker.data.dto.SetChosenTrackResponse
import com.example.playlistmaker.data.dto.SetSearchHistoryRequest
import com.example.playlistmaker.data.dto.SetSearchHistoryResponse

class SharedPrefsClient : NetworkClient {

    val sharedPrefs = Creator.getSharedPrefs()

    override fun doRequest(dto: Any): Response {
        if (dto is ChangeThemeRequest) {
            sharedPrefs.edit().putBoolean(THEME_PREFERENCE_KEY, dto.darkThemeEnabled).apply()
            return ChangeThemeResponse(DarkThemeStateDto(dto.darkThemeEnabled))
        } else if (dto is GetThemeRequest) {
            return GetThemeResponse(
                DarkThemeStateDto(
                    sharedPrefs.getBoolean(
                        THEME_PREFERENCE_KEY,
                        false
                    )
                )
            )
        } else if (dto is GetSearchHistoryRequest) {
            return GetSearchHistoryResponse(
                sharedPrefs.getString(
                    KEY_SEARCH_HISTORY_LIST,
                    dto.elseString
                ).toString()
            )
        } else if (dto is SetSearchHistoryRequest) {
            sharedPrefs.edit()
                .putString(KEY_SEARCH_HISTORY_LIST, dto.list)
                .apply()
            return SetSearchHistoryResponse(dto.list)
        } else if (dto is SetChosenTrackRequest) {
            sharedPrefs.edit()
                .putString(KEY_CHOSEN_TRACK, dto.jsonTrack)
                .apply()
            return SetChosenTrackResponse()
        } else if (dto is GetChosenTrackRequest) {
            return GetChosenTrackResponse(sharedPrefs.getString(KEY_CHOSEN_TRACK, null))
        } else {
            return Response().apply { resultCode = -1 }
        }
    }
}