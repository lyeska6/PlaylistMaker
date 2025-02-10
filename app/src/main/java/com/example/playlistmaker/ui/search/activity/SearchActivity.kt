package com.example.playlistmaker.ui.search.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.audioplayer.activity.AudioplayerActivity
import com.example.playlistmaker.ui.search.view_model.SearchScreenState
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity: AppCompatActivity() {

    private val viewModel by viewModel<SearchViewModel>()
    private lateinit var binding: ActivitySearchBinding
    private val handler = Handler(Looper.getMainLooper())

    private var textSearch = TEXT_SEARCH
    private var isClickAllowed = true

    private val searchedTracksArrayList = ArrayList<Track>()
    private val searchedTracksAdapter = TracksAdapter(
        searchedTracksArrayList
    ) { track ->
        if (clickDebounce()) {
            val playerIntent = Intent(this, AudioplayerActivity::class.java)
            viewModel.addTrackToHistory(track)
            this.startActivity(playerIntent)
        }
    }

    private val historyTrackArrayList = ArrayList<Track>()
    private val searchHistoryAdapter = TracksAdapter(historyTrackArrayList) { track ->
        if (clickDebounce()) {
            val playerIntent = Intent(this, AudioplayerActivity::class.java)
            viewModel.addTrackToHistory(track)
            viewModel.getSearchHistory()
            this.startActivity(playerIntent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.screen_color)
        binding.searchInput.setText(textSearch)

        viewModel.getSearchScreenStateLiveData().observe(this){ state ->
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

        binding.searchHistoryRV.layoutManager = LinearLayoutManager(this@SearchActivity)
        binding.searchHistoryRV.adapter = searchHistoryAdapter

        binding.clearHistoryBut.setOnClickListener {
            viewModel.clearHistory()
        }

        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager

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

        binding.searchInput.doAfterTextChanged { s ->
            textSearch = s.toString()
        }

        binding.searchInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.searchInput.text.isEmpty()) {
                viewModel.getSearchHistory()
            }
        }

        binding.tracksSearchRV.layoutManager = LinearLayoutManager(this@SearchActivity)
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

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
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
        Glide.with(applicationContext)
            .load(R.drawable.nothing_found_error)
            .centerInside()
            .into(binding.searchErrorIcon)
        binding.searchErrorIcon.isVisible = true
        binding.searchErrorText.text = getString(R.string.nothing_found_text)
        binding.searchErrorText.isVisible = true
    }

    private fun consumeNetworkError() {
        consumeDefaultView()
        Glide.with(applicationContext)
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INPUT_SEARCH, textSearch)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        textSearch = savedInstanceState.getString(INPUT_SEARCH, TEXT_SEARCH)
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
        const val INPUT_SEARCH = "INPUT_SEARCH"
        const val TEXT_SEARCH = ""
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}