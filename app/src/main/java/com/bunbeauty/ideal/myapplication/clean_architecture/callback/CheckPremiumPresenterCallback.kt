package com.bunbeauty.ideal.myapplication.clean_architecture.callback

import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service

interface CheckPremiumPresenterCallback {
    fun showError(error:String)
    fun showPremiumActivated(service: Service)
    fun activatePremium()
}