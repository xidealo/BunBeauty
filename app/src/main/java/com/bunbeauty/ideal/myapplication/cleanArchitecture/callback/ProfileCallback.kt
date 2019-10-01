package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback

import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User


interface ProfileCallback {
    fun callbackGetProfileData(user: User)
    fun callbackGetService(service: Service)
}