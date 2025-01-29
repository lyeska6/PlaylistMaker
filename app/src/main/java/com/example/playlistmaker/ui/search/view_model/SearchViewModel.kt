package com.example.playlistmaker.ui.search.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.search.SearchHistoryInteractor
import com.example.playlistmaker.domain.search.TracksInteractor
import com.example.playlistmaker.domain.search.model.Track

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
): ViewModel() {

    private val searchScreenStateLiveData = MutableLiveData<SearchScreenState>(SearchScreenState.Default)

    fun getSearchScreenStateLiveData(): LiveData<SearchScreenState> = searchScreenStateLiveData

    fun search(text: String) {
        searchScreenStateLiveData.postValue(
            SearchScreenState.SearchTracksView(
                isLoading = true,
                nothingFound = false,
                networkError = false,
                tracks = ArrayList()
            )
        )
        tracksInteractor.searchTracks(text,
            foundConsumer = object : TracksInteractor.TracksConsumer {
                override fun consume(tracks: ArrayList<Track>) {
                    if (searchScreenStateLiveData.value is SearchScreenState.SearchTracksView) {
                        searchScreenStateLiveData.postValue(
                            SearchScreenState.SearchTracksView(
                                isLoading = false,
                                nothingFound = false,
                                networkError = false,
                                tracks = tracks
                            )
                        )
                    }
                }
            },
            nothingFoundConsumer = object : TracksInteractor.TracksConsumer {
                override fun consume(tracks: ArrayList<Track>) {
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
                }
            },
            networkErrorConsumer = object : TracksInteractor.TracksConsumer {
                override fun consume(tracks: ArrayList<Track>) {
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
            })
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
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(
                    Creator.provideTracksInteractor(),
                    Creator.provideSearchHistoryInteractor()
                )
            }
        }
    }
}