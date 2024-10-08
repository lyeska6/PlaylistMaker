package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SearchActivity : AppCompatActivity() {

    private var textSearch : String = TEXT_SEARCH

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        window.statusBarColor = ContextCompat.getColor(this, R.color.screen_color)

        val inputSearch = findViewById<EditText>(R.id.searchInput)
        val emptySearch = findViewById<ImageView>(R.id.emptySearch)
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager

        inputSearch.setText(textSearch)

        emptySearch.setOnClickListener{
            inputSearch.setText("")
            inputMethodManager?.hideSoftInputFromWindow(inputSearch.windowToken, 0)
        }

        val textListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //none
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                emptySearch.visibility = emptyButVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {
                textSearch = s.toString()
            }
        }
        inputSearch.addTextChangedListener(textListener)
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