package com.bunbeauty.ideal.myapplication.clean_architecture.domain.service.i_service

import android.content.Intent
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.service.ServicePresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User

interface IServiceUserInteractor {
    fun checkMaster(
        intent: Intent,
        userId: String,
        servicePresenterCallback: ServicePresenterCallback
    )

    fun getUser(): User
}