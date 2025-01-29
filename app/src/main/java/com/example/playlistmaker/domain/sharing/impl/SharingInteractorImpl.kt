package com.example.playlistmaker.domain.sharing.impl

import com.example.playlistmaker.domain.sharing.ExternalNavigator
import com.example.playlistmaker.domain.sharing.SharingRepository
import com.example.playlistmaker.domain.sharing.SharingInteractor

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
    private val sharingRepository: SharingRepository
) : SharingInteractor {

    override fun shareApp() {
        externalNavigator.shareLink(sharingRepository.getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(sharingRepository.getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(sharingRepository.getSupportEmailData())
    }
}