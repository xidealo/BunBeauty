package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views

import com.arellomobile.mvp.MvpView
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

interface ServiceView: MvpView {
    fun showServiceInfo(owner: User, service: Service)
    fun showBottomPanel()
    fun showTopPanelForMyService(serviceId: String, serviceName: String)
    fun showTopPanelForAlienService(serviceName: String, ownerPhotoLink: String, ownerId: String)
    fun showPremium(isPremium: Boolean)
}