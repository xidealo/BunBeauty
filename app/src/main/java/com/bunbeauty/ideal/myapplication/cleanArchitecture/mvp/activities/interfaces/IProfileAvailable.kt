package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

interface IProfileAvailable {
    fun goToProfile(user: User)
}