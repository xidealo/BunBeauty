package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.interfaces

import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User

interface IProfileAvailable {
    fun goToProfile(user: User)
}