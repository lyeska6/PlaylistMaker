package com.example.playlistmaker.domain.sharing

import com.example.playlistmaker.domain.sharing.model.EmailData

interface ExternalNavigator {

    fun shareString(str: String)

    fun openLink(link: String)

    fun openEmail(email: EmailData)
}