package com.example.playlistmaker.ui.search.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.search.SearchHistoryInteractor
import com.example.playlistmaker.domain.search.TracksInteractor
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.utils.debounce
import kotlinx.coroutines.launch

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
): ViewModel() {

    private val searchScreenStateLiveData = MutableLiveData<SearchScreenState>(SearchScreenState.Default)

    fun getSearchScreenStateLiveData(): LiveData<SearchScreenState> = searchScreenStateLiveData

    private var latestSearchText: String? = null

    private fun search(text: String) {
        searchScreenStateLiveData.postValue(
            SearchScreenState.SearchTracksView(
                isLoading = true,
                nothingFound = false,
                networkError = false,
                tracks = ArrayList()
            )
        )

        viewModelScope.launch {
            tracksInteractor.searchTracks(text)
                .collect{ pair ->
                    if (!pair.first.isNullOrEmpty()) {
                        if (searchScreenStateLiveData.value is SearchScreenState.SearchTracksView) {
                            searchScreenStateLiveData.postValue(
                                SearchScreenState.SearchTracksView(
                                    isLoading = false,
                                    nothingFound = false,
                                    networkError = false,
                                    tracks = pair.first!!
                                )
                            )
                        }
                    } else if (pair.second == -1) {
                        if (searchScreenStateLiveData.value is SearchScreenState.SearchTracksView) {
                            searchScreenStateLiveData.postValue(
                                SearchScreenState.SearchTracksView(
                                    isLoading = false,
                                    nothingFound = true,
                                    networkError = false,
                                    tracks = ArrayList()
                                )
                            )
                        }
                    } else {
                        if (searchScreenStateLiveData.value is SearchScreenState.SearchTracksView) {
                            searchScreenStateLiveData.postValue(
                                SearchScreenState.SearchTracksView(
                                    isLoading = false,
                                    nothingFound = false,
                                    networkError = true,
                                    tracks = ArrayList()
                                )
                            )
                        }
                    }
                }
        }
    }

    val searchWithDebounce = debounce<String>(SEARCH_DEBOUNCE_DELAY, viewModelScope, true) { text ->
        if (text != latestSearchText) {
            latestSearchText = text
            search(text)
        }
    }

    fun getSearchHistory() {
        if (!searchHistoryInteractor.getSearchHistory().isNullOrEmpty()) {
            searchScreenStateLiveData.postValue(
                SearchScreenState.SearchHistoryView(
                    searchHistoryInteractor.getSearchHistory()
                )
            )
        }
    }

    fun addTrackToHistory(track: Track) {
        searchHistoryInteractor.addTrack(track)
    }

    fun clearHistory() {
        searchHistoryInteractor.clearHistory()
        searchScreenStateLiveData.postValue(SearchScreenState.Default)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}