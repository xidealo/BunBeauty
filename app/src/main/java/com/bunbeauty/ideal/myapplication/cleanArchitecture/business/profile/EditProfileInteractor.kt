package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile

import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.profile.EditProfilePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

class EditProfileInteractor(private val intent: Intent){
    fun createEditProfileScreen(editProfilePresenterCallback: EditProfilePresenterCallback ) {
        val user = intent.getSerializableExtra(User.USER) as User
        editProfilePresenterCallback.showEditProfile(user)
    }
}