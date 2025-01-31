package com.example.playlistmaker.data.sharing.impl

import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.sharing.SharingRepository
import com.example.playlistmaker.domain.sharing.model.EmailData

class SharingRepositoryImpl(
    private val context: Context
) : SharingRepository {

    override fun getShareAppLink(): String = context.getString(R.string.course_url)

    override fun getTermsLink(): String = context.getString(R.string.terms_link)

    override fun getSupportEmailData(): EmailData {
        return EmailData(
            context.getString(R.string.my_mail),
            context.getString(R.string.mail_subject),
            context.getString(R.string.support_text)
        )
    }
}