package com.bunbeauty.ideal.myapplication.clean_architecture.domain.profile.iProfile

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.profile.ProfilePresenterCallback

interface IProfileOrderInteractor {

    fun getOrderListByUserId(userId: String, profilePresenterCallback: ProfilePresenterCallback)
}