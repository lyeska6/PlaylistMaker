package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Im
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class SettingsActivity:  AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        window.statusBarColor = ContextCompat.getColor(this, R.color.screen_color)

        //val arrowBackButton: ImageView = findViewById<ImageView>(R.id.arrowBack)
        //arrowBackButton.setOnClickListener{
        //    startActivity(Intent(this, MainActivity::class.java))
        //}
        //shareButton.setOnClickListener {
        //            val message = "Привет, Android разработка — это круто!"
        //            val shareIntent = Intent(Intent.ACTION_SENDTO)
        //            shareIntent.data = Uri.parse("mailto:")
        //            shareIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("yourEmail@ya.ru"))
        //            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
        //            startActivity(shareIntent)
        //        }

        val shareApp = findViewById<ImageView>(R.id.shareApp)
        shareApp.setOnClickListener {
            val message = R.string.course_url
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
            shareIntent.setType("text/plain")
            startActivity(Intent.createChooser(shareIntent,"Выберите способ отправки"))
        }

        val supportBut = findViewById<ImageView>(R.id.supportBut)
        supportBut.setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SEND)
            supportIntent.data = Uri.parse("mailto:")
            supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(R.string.my_mail))
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.mail_subject)
            supportIntent.putExtra(Intent.EXTRA_TEXT, R.string.support_text)
            startActivity(supportIntent)
        }

        val agreementBut = findViewById<ImageView>(R.id.agreementBut)
        agreementBut.setOnClickListener {
            val agreementIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://yandex.ru/legal/practicum_offer/"))
            startActivity(agreementIntent)
        }

    }
}