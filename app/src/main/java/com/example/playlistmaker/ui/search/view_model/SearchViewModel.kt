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

    private val searchedTracksLiveData = MutableLiveData(ArrayList<Track>())
    private val searchHistoryListLiveData = MutableLiveData(searchHistoryInteractor.getSearchHistory())
    private val searchScreenStateLiveData = MutableLiveData<SearchScreenState>(SearchScreenState.Default)

    fun getSearchedTracksLiveData(): LiveData<ArrayList<Track>> = searchedTracksLiveData
    fun getSearchHistoryListLiveData(): LiveData<ArrayList<Track>> = searchHistoryListLiveData
    fun getSearchScreenStateLiveData(): LiveData<SearchScreenState> = searchScreenStateLiveData

    fun search(text: String) {
        searchScreenStateLiveData.postValue(
            SearchScreenState.SearchTracksView(
                isLoading = true,
                nothingFound = false,
                networkError = false
            )
        )
        tracksInteractor.searchTracks(text,
            foundConsumer = object : TracksInteractor.TracksConsumer {
                override fun consume(tracks: ArrayList<Track>) {
                    searchScreenStateLiveData.postValue(
                        SearchScreenState.SearchTracksView(
                            isLoading = false,
                            nothingFound = false,
                            networkError = false
                        )
                    )
                    searchedTracksLiveData.postValue(tracks)
                }
            },
            nothingFoundConsumer = object : TracksInteractor.TracksConsumer {
                override fun consume(tracks: ArrayList<Track>) {
                    searchScreenStateLiveData.postValue(
                        SearchScreenState.SearchTracksView(
                            isLoading = false,
                            nothingFound = true,
                            networkError = false
                        )
                    )
                }
            },
            networkErrorConsumer = object : TracksInteractor.TracksConsumer {
                override fun consume(tracks: ArrayList<Track>) {
                    searchScreenStateLiveData.postValue(
                        SearchScreenState.SearchTracksView(
                            isLoading = false,
                            nothingFound = false,
                            networkError = true
                        )
                    )
                }
            })
    }

    fun getSearchHistory() {
        searchHistoryListLiveData.postValue(searchHistoryInteractor.getSearchHistory())
        searchScreenStateLiveData.postValue(SearchScreenState.SearchHistoryView)
    }

    fun addTrackToHistory(track: Track) {
        searchHistoryInteractor.addTrack(track)
        searchHistoryListLiveData.postValue(searchHistoryInteractor.getSearchHistory())
    }

    fun clearHistory() {
        searchHistoryInteractor.clearHistory()
        searchHistoryListLiveData.postValue(searchHistoryInteractor.getSearchHistory())
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