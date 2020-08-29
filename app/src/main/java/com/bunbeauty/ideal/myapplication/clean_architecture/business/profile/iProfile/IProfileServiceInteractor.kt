package com.bunbeauty.ideal.myapplication.clean_architecture.business.profile.iProfile

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.profile.ProfilePresenterCallback

interface IProfileServiceInteractor {

    fun getServicesByUserId(userId: String?, profilePresenterCallback: ProfilePresenterCallback)
}