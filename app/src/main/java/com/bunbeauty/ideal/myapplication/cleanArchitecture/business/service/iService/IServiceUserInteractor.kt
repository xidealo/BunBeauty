package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service.iService

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

interface IServiceUserInteractor {
    fun getUser(): User
}