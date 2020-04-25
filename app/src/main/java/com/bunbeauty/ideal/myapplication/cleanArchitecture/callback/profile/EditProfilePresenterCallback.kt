package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.profile

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

interface EditProfilePresenterCallback {
    fun showEditProfile(user: User)
}