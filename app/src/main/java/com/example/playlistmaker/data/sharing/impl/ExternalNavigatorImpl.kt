package com.example.playlistmaker.data.sharing.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.domain.sharing.ExternalNavigator
import com.example.playlistmaker.domain.sharing.model.EmailData

class ExternalNavigatorImpl(val context: Context): ExternalNavigator {

    override fun shareLink(link: String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.putExtra(Intent.EXTRA_TEXT, link)
        shareIntent.setType("text/plain")
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(Intent.createChooser(shareIntent, "Выберите способ отправки").setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    override fun openLink(link: String) {
        val agreementIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse(link))
        agreementIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(agreementIntent)
    }

    override fun openEmail(email: EmailData) {
        val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, email.email)
            putExtra(Intent.EXTRA_SUBJECT, email.subject)
            putExtra(Intent.EXTRA_TEXT, email.text)
        }
        supportIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(supportIntent)
    }
}