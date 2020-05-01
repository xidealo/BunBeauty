package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.editing.EditProfileInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.profile.EditProfilePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.EditProfileView


@InjectViewState
class EditProfilePresenter(private val editProfileInteractor: EditProfileInteractor) :
    MvpPresenter<EditProfileView>(), EditProfilePresenterCallback {
    fun createEditProfileScreen() {
        editProfileInteractor.createEditProfileScreen(this)
    }

    override fun showEditProfile(user: User) {
        viewState.showEditProfile(user)
    }

    fun saveData(name: String, surname: String, city: String, phone: String) {
        val user = User()
        user.name = name
        user.surname = surname
        user.city = city
        user.phone = phone
        editProfileInteractor.saveData(user,this)
    }
}