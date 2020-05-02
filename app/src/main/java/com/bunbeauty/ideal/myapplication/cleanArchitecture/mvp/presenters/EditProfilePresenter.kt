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
        val user = editProfileInteractor.cacheUser
        user.name = name
        user.surname = surname
        user.city = city
        user.phone = phone
        editProfileInteractor.saveData(user, this)
    }

    override fun nameEditProfileInputError() {
        viewState.enableEditProfileEditButton()
        viewState.setNameEditProfileInputError("Допустимы только буквы и тире")

    }

    override fun nameEditProfileInputErrorEmpty() {
        viewState.enableEditProfileEditButton()
        viewState.setNameEditProfileInputError("Введите своё имя")
    }

    override fun nameEditProfileInputErrorLong() {
        viewState.enableEditProfileEditButton()
        viewState.setNameEditProfileInputError("Слишком длинное имя")
    }

    override fun surnameEditProfileInputError() {
        viewState.enableEditProfileEditButton()
        viewState.setSurnameEditProfileInputError("Допустимы только буквы и тире")
    }

    override fun surnameEditProfileInputErrorEmpty() {
        viewState.enableEditProfileEditButton()
        viewState.setSurnameEditProfileInputError("Введите свою фамилию")
    }

    override fun surnameEditProfileEditErrorLong() {
        viewState.enableEditProfileEditButton()
        viewState.setSurnameEditProfileInputError("Слишком длинная фамилия")
    }

    override fun cityEditProfileInputError() {
        viewState.enableEditProfileEditButton()
        viewState.showNoSelectedCity()
    }

    override fun phoneEditProfileInputError() {
        viewState.enableEditProfileEditButton()
        viewState.showPhoneError("Некорректный номер телефона")
    }
}