package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class SettingsActivity:  AppCompatActivity() {
    @SuppressLint("QueryPermissionsNeeded")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        window.statusBarColor = ContextCompat.getColor(this, R.color.screen_color)

        val shareApp = findViewById<ImageView>(R.id.shareApp)
        shareApp.setOnClickListener {
            val message = getString(R.string.course_url)
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
            shareIntent.setType("text/plain")
            startActivity(Intent.createChooser(shareIntent,"Выберите способ отправки"))
        }

        val supportBut = findViewById<ImageView>(R.id.supportBut)
        supportBut.setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.my_mail)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mail_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.support_text))
            }
            startActivity(supportIntent)
        }

        val agreementBut = findViewById<ImageView>(R.id.agreementBut)
        agreementBut.setOnClickListener {
            val agreementIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://yandex.ru/legal/practicum_offer/"))
            startActivity(agreementIntent)
        }

    }
}