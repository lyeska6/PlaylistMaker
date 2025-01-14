package com.example.playlistmaker.ui.searchPage

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.Creator
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.models.Track

class SearchActivity : AppCompatActivity() {

    private var textSearch: String = TEXT_SEARCH

    private val handler = Handler(Looper.getMainLooper())

    private val searchedTracksArrayList = ArrayList<Track>()
    private lateinit var searchedTracksAdapter: SearchedTracksAdapter

    private lateinit var searchedTracks: RecyclerView
    private lateinit var errorIcon: ImageView
    private lateinit var errorText: TextView
    private lateinit var errorRefreshBut: Button
    private lateinit var progressBar: ProgressBar

    private val tracksInteractor = Creator.provideTracksInteractor()
    private val searchHistoryInteractor = Creator.provideSearchHistoryInteractor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        window.statusBarColor = ContextCompat.getColor(this, R.color.screen_color)

        val inputSearch = findViewById<EditText>(R.id.searchInput)
        val emptySearchBut = findViewById<ImageView>(R.id.emptySearch)

        val searchHistoryRV = findViewById<RecyclerView>(R.id.searchHistoryRV)
        searchHistoryRV.layoutManager = LinearLayoutManager(this@SearchActivity)

        val searchHistoryAdapter = SearchHistoryAdapter(this,
            { searchHistoryInteractor.getSearchHistory() },
            { track -> searchHistoryInteractor.addTrack(track) })
        searchHistoryRV.adapter = searchHistoryAdapter

        val searchHistoryLayout = findViewById<LinearLayout>(R.id.searchHistoryAll)
        val clearSearchHistoryBut = findViewById<Button>(R.id.clearHistoryBut)
        clearSearchHistoryBut.setOnClickListener {
            searchHistoryInteractor.clearHistory()
            searchHistoryLayout.isVisible = false
        }

        errorIcon = findViewById(R.id.searchErrorIcon)
        errorText = findViewById(R.id.searchErrorText)
        errorRefreshBut = findViewById(R.id.searchErrorBut)
        progressBar = findViewById(R.id.progressBar)

        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager

        inputSearch.setText(textSearch)

        fun consumeFoundTracks(foundTracks: List<Track>) {
            handler.post {
                searchedTracksArrayList.clear()
                searchedTracksArrayList.addAll(foundTracks)
                searchedTracksAdapter.notifyDataSetChanged()
                progressBar.isVisible = false
                searchedTracks.isVisible = true
            }
        }

        fun consumeNothingFound() {
            handler.post {
                progressBar.isVisible = false
                Glide.with(applicationContext).load(R.drawable.nothing_found_error).centerInside()
                    .into(errorIcon)
                errorIcon.isVisible = true
                errorText.text = getString(R.string.nothing_found_text)
                errorText.isVisible = true
            }
        }

        fun consumeNetworkError() {
            handler.post {
                progressBar.isVisible = false
                Glide.with(applicationContext).load(R.drawable.server_connection_error)
                    .centerInside().into(errorIcon)
                errorIcon.isVisible = true
                errorText.text = getString(R.string.server_connection_error_text)
                errorText.isVisible = true
                errorRefreshBut.isVisible = true
            }
        }

        fun searchTracksByInteractor(text: String) {
            if (text.isNotEmpty()) {
                searchedTracks.isVisible = false
                errorIcon.isVisible = false
                errorRefreshBut.isVisible = false
                errorText.isVisible = false
                progressBar.isVisible = true
                tracksInteractor.searchTracks(text,
                    foundConsumer = object : TracksInteractor.TracksConsumer {
                        override fun consume(foundTracks: List<Track>) {
                            consumeFoundTracks(foundTracks)
                        }
                    },
                    nothingFoundConsumer = object : TracksInteractor.TracksConsumer {
                        override fun consume(tracks: List<Track>) {
                            consumeNothingFound()
                        }
                    },
                    networkErrorConsumer = object : TracksInteractor.TracksConsumer {
                        override fun consume(tracks: List<Track>) {
                            consumeNetworkError()
                        }
                    })
            }
        }

        val searchRunnable = Runnable {
            inputMethodManager?.hideSoftInputFromWindow(inputSearch.windowToken, 0)
            searchTracksByInteractor(inputSearch.text.toString())
        }

        fun searchDebounce() {
            handler.removeCallbacks(searchRunnable)
            handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
        }

        emptySearchBut.setOnClickListener {
            handler.removeCallbacks(searchRunnable)
            inputSearch.setText("")
            inputMethodManager?.hideSoftInputFromWindow(inputSearch.windowToken, 0)
            errorIcon.isVisible = false
            errorRefreshBut.isVisible = false
            errorText.isVisible = false
            progressBar.isVisible = false
            searchedTracks.isVisible = false
            searchHistoryAdapter.notifyDataSetChanged()
        }

        val textListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //none
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                emptySearchBut.isVisible = emptyButVisibility(s)
                if (inputSearch.hasFocus() && inputSearch.text.isEmpty() && !searchHistoryInteractor.getSearchHistory().isNullOrEmpty()) {
                    searchHistoryLayout.isVisible = true
                    searchedTracks.isVisible = false
                } else {
                    searchHistoryLayout.isVisible = false
                }
                searchDebounce()
            }

            override fun afterTextChanged(s: Editable?) {
                textSearch = s.toString()
            }
        }
        inputSearch.addTextChangedListener(textListener)

        inputSearch.setOnFocusChangeListener { _, hasFocus ->
            searchHistoryLayout.isVisible =
                hasFocus && inputSearch.text.isEmpty() && !searchHistoryInteractor.getSearchHistory().isNullOrEmpty()
        }

        searchedTracks = findViewById(R.id.tracksSearchRV)
        searchedTracks.layoutManager = LinearLayoutManager(this@SearchActivity)

        searchedTracksAdapter = SearchedTracksAdapter(this, searchedTracksArrayList) { track ->
            searchHistoryInteractor.addTrack(
                track
            )
        }
        searchedTracks.adapter = searchedTracksAdapter

        inputSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                inputMethodManager?.hideSoftInputFromWindow(inputSearch.windowToken, 0)
                handler.removeCallbacks(searchRunnable)
                searchTracksByInteractor(inputSearch.text.toString())
                true
            }
            false
        }

        errorRefreshBut.setOnClickListener {
            handler.removeCallbacks(searchRunnable)
            searchTracksByInteractor(inputSearch.text.toString())
        }

        val backBut = findViewById<ImageView>(R.id.buttonBack)
        backBut.setOnClickListener {
            onBackPressed()
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

    companion object {
        const val INPUT_SEARCH = "INPUT_SEARCH"
        const val TEXT_SEARCH = ""
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}