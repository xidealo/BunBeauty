package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views

import com.arellomobile.mvp.MvpView
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User

interface ProfileView: MvpView {
    fun showMyProfileView()
    fun showAlienProfileView()
    fun showUserInfo(user: User)
    fun showUserServices(services: List<Service>)

}