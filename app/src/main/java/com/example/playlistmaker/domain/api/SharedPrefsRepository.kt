package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.DarkThemeState
import com.example.playlistmaker.domain.models.Track

interface SharedPrefsRepository {
    fun changeTheme(darkThemeEnabled: Boolean): DarkThemeState

    fun getTheme(): DarkThemeState

    fun getSearchHistory(elseString: String): String

    fun setSearchHistory(jsonList: String): String

    fun setChosenTrack(jsonTrack: String)

    fun getChosenTrack(): Track
}