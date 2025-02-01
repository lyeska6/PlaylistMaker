package com.example.playlistmaker.domain.sharing

import com.example.playlistmaker.domain.sharing.model.EmailData

interface SharingRepository {

    fun getShareAppLink(): String

    fun getTermsLink(): String

    fun getSupportEmailData(): EmailData
}