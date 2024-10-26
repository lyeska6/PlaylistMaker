package com.example.playlistmaker

import android.content.Context
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
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private var textSearch : String = TEXT_SEARCH

    private val itunesBaseUrl : String = "https://itunes.apple.com/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val tracksSearchService = retrofit.create(ITunesApi::class.java)

    private val trackArrayList = ArrayList<Track>()
    lateinit var tracksAdapter : TrackAdapter

    lateinit var searchTracks: RecyclerView
    lateinit var errorIcon: ImageView
    lateinit var errorText: TextView
    lateinit var errorRefreshBut: Button

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
                            Log.d("My_LOG", "чето ниче не найдено")// заглушка картинка что ниче не найдено
                            searchTracks.visibility = View.GONE
                            errorRefreshBut.visibility = View.GONE
                            Glide.with(applicationContext).load(R.drawable.nothing_found_error).centerInside().into(errorIcon)
                            errorIcon.visibility = View.VISIBLE
                            errorText.setText(nothingFoundText)
                            errorText.visibility = View.VISIBLE
                        } else {
                            Log.d("My_LOG", "все найдено, пуляем в эррэй")
                            errorIcon.visibility = View.GONE
                            errorRefreshBut.visibility = View.GONE
                            errorText.visibility = View.GONE
                            trackArrayList.clear()
                            trackArrayList.addAll(response.body()?.results!!)
                            tracksAdapter.notifyDataSetChanged()
                            searchTracks.visibility = View.VISIBLE
                        }
                    } else {
                        Log.d("My_LOG", "Маловероятно, но интернета типо нет или с Api проблемы")// заглушка что проблема с поиском
                        searchTracks.visibility = View.GONE
                        Glide.with(applicationContext).load(R.drawable.server_connection_error).centerInside().into(errorIcon)
                        errorIcon.visibility = View.VISIBLE
                        errorText.setText(serverConnectionText)
                        errorText.visibility = View.VISIBLE
                        errorRefreshBut.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                    Log.d("My_LOG", "Маловероятно, но интернета типо нет или с Api проблемы")// та же заглушка проблем с поиском
                    //"Проблемы со связью\n\nЗагрузка не удалась. Проверьте подключение к интернету"
                    Log.d("My_LOG", "Маловероятно, но интернета типо нет или с Api проблемы")// заглушка что проблема с поиском
                    searchTracks.visibility = View.GONE
                    Glide.with(applicationContext).load(R.drawable.server_connection_error).centerInside().into(errorIcon)
                    errorIcon.visibility = View.VISIBLE
                    errorText.setText(serverConnectionText)
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
            trackArrayList.clear()
            tracksAdapter.notifyDataSetChanged()
            searchTracks.visibility = View.VISIBLE
        }

        val textListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //none
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                emptySearchBut.visibility = emptyButVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {
                textSearch = s.toString()
            }
        }
        inputSearch.addTextChangedListener(textListener)

        searchTracks = findViewById<RecyclerView>(R.id.tracksSearchRV)
        searchTracks.layoutManager = LinearLayoutManager(this@SearchActivity)

        tracksAdapter = TrackAdapter(trackArrayList)
        searchTracks.adapter =  tracksAdapter

        inputSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                inputMethodManager?.hideSoftInputFromWindow(inputSearch.windowToken, 0)
                // ВЫПОЛНЯЙТЕ ПОИСКОВЫЙ ЗАПРОС ЗДЕСЬ
                searchTracks(inputSearch.text.toString())
                true
            }
            false
        }

        errorRefreshBut.setOnClickListener{
            searchTracks(inputSearch.text.toString())
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