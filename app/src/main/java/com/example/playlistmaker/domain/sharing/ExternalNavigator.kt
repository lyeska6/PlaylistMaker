package com.example.playlistmaker.domain.sharing

import com.example.playlistmaker.domain.sharing.model.EmailData

interface ExternalNavigator {

    fun shareLink(link: String)

    fun openLink(link: String)

    fun openEmail(email: EmailData)
}