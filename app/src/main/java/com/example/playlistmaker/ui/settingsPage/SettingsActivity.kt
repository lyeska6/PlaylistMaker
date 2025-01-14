package com.example.playlistmaker.ui.settingsPage

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.playlistmaker.App
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.DarkThemeInteractor
import com.example.playlistmaker.domain.models.DarkThemeState
import com.google.android.material.switchmaterial.SwitchMaterial


class SettingsActivity : AppCompatActivity() {

    private val darkThemeInteractor = Creator.provideDarkThemeInteractor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        window.statusBarColor = ContextCompat.getColor(this, R.color.screen_color)

        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)
        themeSwitcher.setChecked(darkThemeInteractor.getTheme().currentState)

        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            darkThemeInteractor.changeTheme(
                checked,
                object : DarkThemeInteractor.ChangeThemeConsumer {
                    override fun consume(darkThemeState: DarkThemeState) {
                        (applicationContext as App).switchTheme(darkThemeState.currentState)
                    }
                })
        }

        val shareApp = findViewById<ImageView>(R.id.shareApp)
        shareApp.setOnClickListener {
            val message = getString(R.string.course_url)
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
            shareIntent.setType("text/plain")
            startActivity(Intent.createChooser(shareIntent, "Выберите способ отправки"))
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
            val agreementIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://yandex.ru/legal/practicum_offer/"))
            startActivity(agreementIntent)
        }

        val backBut = findViewById<ImageView>(R.id.buttonBack)
        backBut.setOnClickListener {
            onBackPressed()
        }
    }
}