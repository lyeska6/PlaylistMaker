package com.example.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Visibility
import com.bumptech.glide.Glide
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.Key

class SearchActivity : AppCompatActivity() {

    private var textSearch : String = TEXT_SEARCH

    private val itunesBaseUrl : String = "https://itunes.apple.com/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val tracksSearchService = retrofit.create(ITunesApi::class.java)

    private val searchedTracksArrayList = ArrayList<Track>()
    lateinit var searchedTracksAdapter : SearchedTracksAdapter

    lateinit var searchedTracks: RecyclerView
    lateinit var errorIcon: ImageView
    lateinit var errorText: TextView
    lateinit var errorRefreshBut: Button

    lateinit var listener : SharedPreferences.OnSharedPreferenceChangeListener

    fun searchTracks(text: String){
        val nothingFoundText = "Ничего не нашлось"
        val serverConnectionText = "Проблемы со связью\n\nЗагрузка не удалась. Проверьте подключение к интернету"

        tracksSearchService.searchTracks(text)
            .enqueue(object : Callback<TracksResponse>{
                override fun onResponse(
                    call: Call<TracksResponse>,
                    response: Response<TracksResponse>
                ) {
                    if (response.code() == 200) {
                        if (response.body()?.results.isNullOrEmpty()) {
                            searchedTracks.visibility = View.GONE
                            errorRefreshBut.visibility = View.GONE
                            Glide.with(applicationContext).load(R.drawable.nothing_found_error).centerInside().into(errorIcon)
                            errorIcon.visibility = View.VISIBLE
                            errorText.text = nothingFoundText
                            errorText.visibility = View.VISIBLE
                        } else {
                            errorIcon.visibility = View.GONE
                            errorRefreshBut.visibility = View.GONE
                            errorText.visibility = View.GONE
                            searchedTracksArrayList.clear()
                            searchedTracksArrayList.addAll(response.body()?.results!!)
                            searchedTracksAdapter.notifyDataSetChanged()
                            searchedTracks.visibility = View.VISIBLE
                        }
                    } else {
                        searchedTracks.visibility = View.GONE
                        Glide.with(applicationContext).load(R.drawable.server_connection_error).centerInside().into(errorIcon)
                        errorIcon.visibility = View.VISIBLE
                        errorText.text = serverConnectionText
                        errorText.visibility = View.VISIBLE
                        errorRefreshBut.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                    Log.d("My_LOG", "Маловероятно, но интернета типо нет или с Api проблемы")// та же заглушка проблем с поиском
                    //"Проблемы со связью\n\nЗагрузка не удалась. Проверьте подключение к интернету"
                    Log.d("My_LOG", "Маловероятно, но интернета типо нет или с Api проблемы")// заглушка что проблема с поиском
                    searchedTracks.visibility = View.GONE
                    Glide.with(applicationContext).load(R.drawable.server_connection_error).centerInside().into(errorIcon)
                    errorIcon.visibility = View.VISIBLE
                    errorText.text = serverConnectionText
                    errorText.visibility = View.VISIBLE
                    errorRefreshBut.visibility = View.VISIBLE
                }
            })

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        window.statusBarColor = ContextCompat.getColor(this, R.color.screen_color)

        val inputSearch = findViewById<EditText>(R.id.searchInput)
        val emptySearchBut = findViewById<ImageView>(R.id.emptySearch)

        val searchHistoryRV = findViewById<RecyclerView>(R.id.searchHistoryRV)
        searchHistoryRV.layoutManager = LinearLayoutManager(this@SearchActivity)

        val sharedPrefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)
        val searchHistoryData = SearchHistory(sharedPrefs)
        searchHistoryData.setArray()
        val searchHistoryAdapter = SearchHistoryAdapter(sharedPrefs, searchHistoryData)
        searchHistoryRV.adapter = searchHistoryAdapter

        listener = SharedPreferences.OnSharedPreferenceChangeListener{ sharedPreferences, key ->
            if (key == KEY_SEARCH_HISTORY_LIST) {
                searchHistoryData.setArray(searchHistoryData.getSharedPrefs(key))
                searchHistoryAdapter.notifyDataSetChanged()
            }
        }
        sharedPrefs.registerOnSharedPreferenceChangeListener(listener)

        val searchHistoryLayout = findViewById<LinearLayout>(R.id.searchHistoryAll)
        val clearSearchHistoryBut = findViewById<Button>(R.id.clearHistoryBut)
        clearSearchHistoryBut.setOnClickListener {
            searchHistoryData.clearHistory()
            searchHistoryLayout.visibility = View.GONE
        }

        errorIcon = findViewById<ImageView>(R.id.searchErrorIcon)
        errorText = findViewById<TextView>(R.id.searchErrorText)
        errorRefreshBut = findViewById<Button>(R.id.searchErrorBut)

        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager

        inputSearch.setText(textSearch)

        emptySearchBut.setOnClickListener {
            inputSearch.setText("")
            inputMethodManager?.hideSoftInputFromWindow(inputSearch.windowToken, 0)
            errorIcon.visibility = View.GONE
            errorRefreshBut.visibility = View.GONE
            errorText.visibility = View.GONE
            searchedTracksArrayList.clear()
            searchedTracksAdapter.notifyDataSetChanged()
            searchedTracks.visibility = View.GONE
            searchHistoryData.setArray()
            searchHistoryAdapter.notifyDataSetChanged()
        }

        val textListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //none
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                emptySearchBut.visibility = emptyButVisibility(s)
                val newData = searchHistoryData.jsonToArrayList(searchHistoryData.getSharedPrefs(KEY_SEARCH_HISTORY_LIST))
                searchHistoryLayout.visibility = if (inputSearch.hasFocus() && inputSearch.text.isEmpty() && !newData.isNullOrEmpty()) View.VISIBLE else View.GONE
            }

            override fun afterTextChanged(s: Editable?) {
                textSearch = s.toString()
            }
        }
        inputSearch.addTextChangedListener(textListener)

        inputSearch.setOnFocusChangeListener{view, hasFocus ->
            val newData = searchHistoryData.jsonToArrayList(searchHistoryData.getSharedPrefs(KEY_SEARCH_HISTORY_LIST))
            searchHistoryLayout.visibility = if (hasFocus && inputSearch.text.isEmpty() && !newData.isNullOrEmpty()) View.VISIBLE else View.GONE
        }

        searchedTracks = findViewById<RecyclerView>(R.id.tracksSearchRV)
        searchedTracks.layoutManager = LinearLayoutManager(this@SearchActivity)

        searchedTracksAdapter = SearchedTracksAdapter(searchedTracksArrayList, searchHistoryData)
        searchedTracks.adapter = searchedTracksAdapter

        inputSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                inputMethodManager?.hideSoftInputFromWindow(inputSearch.windowToken, 0)
                searchTracks(inputSearch.text.toString())
                true
            }
            false
        }

        errorRefreshBut.setOnClickListener{
            searchTracks(inputSearch.text.toString())
        }

        val backBut = findViewById<ImageView>(R.id.buttonBack)
        backBut.setOnClickListener {
            onBackPressed()
        }

    }

    private fun emptyButVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
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
    }
}