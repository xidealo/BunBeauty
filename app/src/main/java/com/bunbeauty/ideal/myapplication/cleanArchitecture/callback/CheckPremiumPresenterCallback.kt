package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service

interface CheckPremiumPresenterCallback {
    fun showError(error:String)
    fun showPremiumActivated(service: Service)
    fun activatePremium()
}