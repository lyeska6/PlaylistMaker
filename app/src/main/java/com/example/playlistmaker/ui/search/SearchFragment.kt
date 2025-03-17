package com.example.playlistmaker.ui.search

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.search.activity.TracksAdapter
import com.example.playlistmaker.ui.search.view_model.SearchScreenState
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment: Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private val viewModel by viewModel<SearchViewModel>()
    private val handler = Handler(Looper.getMainLooper())

    private var isClickAllowed = true

    private val searchedTracksArrayList = ArrayList<Track>()
    private val searchedTracksAdapter = TracksAdapter(
        searchedTracksArrayList
    ) { track ->
        if (clickDebounce()) {
            viewModel.addTrackToHistory(track)

            findNavController().navigate(R.id.action_searchFragment_to_audioplayerActivity)
        }
    }

    private val historyTrackArrayList = ArrayList<Track>()
    private val searchHistoryAdapter = TracksAdapter(historyTrackArrayList) { track ->
        if (clickDebounce()) {
            viewModel.addTrackToHistory(track)
            viewModel.getSearchHistory()
            findNavController().navigate(R.id.action_searchFragment_to_audioplayerActivity)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.getSearchScreenStateLiveData().observe(viewLifecycleOwner){ state ->
            when (state) {
                SearchScreenState.Default -> {
                    consumeDefaultView()
                }
                is SearchScreenState.SearchHistoryView -> {
                    consumeSearchHistoryView(state.tracks)
                }
                is SearchScreenState.SearchTracksView -> {
                    if (state.isLoading) {
                        consumeIsLoading()
                    } else if (state.nothingFound) {
                        consumeNothingFound()
                    } else if (state.networkError) {
                        consumeNetworkError()
                    } else {
                        consumeFoundTracks(state.tracks)
                    }
                }
            }
        }

        binding.searchHistoryRV.layoutManager = LinearLayoutManager(requireContext())
        binding.searchHistoryRV.adapter = searchHistoryAdapter

        binding.clearHistoryBut.setOnClickListener {
            viewModel.clearHistory()
        }

        val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager

        val searchRunnable = Runnable {
            inputMethodManager?.hideSoftInputFromWindow(binding.searchInput.windowToken, 0)
            searchTracksByViewModel(binding.searchInput.text.toString())
        }
        fun searchDebounce() {
            handler.removeCallbacks(searchRunnable)
            handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
        }

        binding.clearSearchInputBut.setOnClickListener {
            handler.removeCallbacks(searchRunnable)
            binding.searchInput.setText("")
            inputMethodManager?.hideSoftInputFromWindow(binding.searchInput.windowToken, 0)
            viewModel.getSearchHistory()
        }

        binding.searchInput.doOnTextChanged { s, _, _, _ ->
            binding.clearSearchInputBut.isVisible = emptyButVisibility(s)
            if (binding.searchInput.hasFocus() && s.isNullOrEmpty()) {
                viewModel.getSearchHistory()
            } else {
                consumeDefaultView()
            }
            searchDebounce()
        }

        binding.searchInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.searchInput.text.isEmpty()) {
                viewModel.getSearchHistory()
            }
        }

        binding.tracksSearchRV.layoutManager = LinearLayoutManager(requireContext())
        binding.tracksSearchRV.adapter = searchedTracksAdapter

        binding.searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                inputMethodManager?.hideSoftInputFromWindow(binding.searchInput.windowToken, 0)
                handler.removeCallbacks(searchRunnable)
                searchTracksByViewModel(binding.searchInput.text.toString())
            }
            false
        }

        binding.searchErrorBut.setOnClickListener {
            handler.removeCallbacks(searchRunnable)
            searchTracksByViewModel(binding.searchInput.text.toString())
        }
    }

    private fun consumeDefaultView() {
        binding.searchHistory.isVisible = false
        binding.tracksSearchRV.isVisible = false
        binding.searchErrorIcon.isVisible = false
        binding.searchErrorBut.isVisible = false
        binding.searchErrorText.isVisible = false
        binding.progressBar.isVisible = false
    }

    private fun consumeSearchHistoryView(tracks: ArrayList<Track>) {
        consumeDefaultView()
        historyTrackArrayList.clear()
        historyTrackArrayList.addAll(tracks)
        searchHistoryAdapter.notifyDataSetChanged()
        binding.searchHistory.isVisible = true
    }

    private fun consumeIsLoading() {
        consumeDefaultView()
        binding.progressBar.isVisible = true
    }

    private fun consumeNothingFound() {
        consumeDefaultView()
        Glide.with(requireContext())
            .load(R.drawable.nothing_found_error)
            .centerInside()
            .into(binding.searchErrorIcon)
        binding.searchErrorIcon.isVisible = true
        binding.searchErrorText.text = getString(R.string.nothing_found_text)
        binding.searchErrorText.isVisible = true
    }

    private fun consumeNetworkError() {
        consumeDefaultView()
        Glide.with(requireContext())
            .load(R.drawable.server_connection_error)
            .centerInside()
            .into(binding.searchErrorIcon)
        binding.searchErrorIcon.isVisible = true
        binding.searchErrorText.text = getString(R.string.server_connection_error_text)
        binding.searchErrorText.isVisible = true
        binding.searchErrorBut.isVisible = true
    }

    private fun consumeFoundTracks(foundTracks: List<Track>) {
        consumeDefaultView()
        searchedTracksArrayList.clear()
        searchedTracksArrayList.addAll(foundTracks)
        searchedTracksAdapter.notifyDataSetChanged()
        binding.tracksSearchRV.isVisible = true
    }

    private fun searchTracksByViewModel(text: String) {
        if (text.isNotEmpty()) {
            viewModel.search(text)
        }
    }

    private fun emptyButVisibility(s: CharSequence?): Boolean {
        return !s.isNullOrEmpty()
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}