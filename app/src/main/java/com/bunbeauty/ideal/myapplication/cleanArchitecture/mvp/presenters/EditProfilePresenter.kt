package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.editing.profile.EditProfileInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.profile.EditProfilePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.EditProfileView


@InjectViewState
class EditProfilePresenter(private val editProfileInteractor: EditProfileInteractor) :
    MvpPresenter<EditProfileView>(), EditProfilePresenterCallback {

    fun getUser() {
        editProfileInteractor.getUser(this)
    }

    override fun showEditProfile(user: User) {
        viewState.showEditProfile(user)
    }

    override fun goToProfile(user: User) {
        viewState.goToProfile(user)
    }

    fun saveData(name: String, surname: String, city: String, phone: String) {
        val user = editProfileInteractor.cacheUser.copy()
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

    override fun phoneEditProfileInputError() {
        viewState.enableEditProfileEditButton()
        viewState.showPhoneError("Некорректный номер телефона")
    }

    override fun returnCodeSent() {
        viewState.showCodeInputAndButtons()
    }

    fun resendCode(phoneNumber: String) {
        editProfileInteractor.resendCode(phoneNumber)
    }

    fun verifyCode(code: String) {
        editProfileInteractor.verifyCode(code)
    }

    override fun showTooManyRequestsError() {
        viewState.showMessage("Лимит привышен. Повторите попытку позже")
    }

    override fun showTooShortCodeError() {
        viewState.showMessage("Код слишком короткий")
    }

    override fun showVerificationFailed() {
        viewState.showMessage("Что-то пошло не так")
    }

    override fun showWrongCodeError() {
        viewState.showMessage("Неверный код. Попробуйте ещё раз.")
    }

    override fun showPhoneAlreadyUsedError() {
        viewState.showMessage("Данный номер уже используется другим пользователем.")
    }

    fun signOut() {
        editProfileInteractor.signOut()
    }
}