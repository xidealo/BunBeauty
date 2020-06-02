package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service

import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service.iService.IServiceUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

class ServiceUserInteractor(private val intent: Intent) : IServiceUserInteractor {
    override fun getUser() = intent.getSerializableExtra(User.USER) as User
}