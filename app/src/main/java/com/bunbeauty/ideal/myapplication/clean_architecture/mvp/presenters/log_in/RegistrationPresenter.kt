package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.log_in

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.business.log_in.iLogIn.IRegistrationUserInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.logIn.RegistrationPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.log_in.RegistrationView

@InjectViewState
class RegistrationPresenter(private val registrationUserInteractor: IRegistrationUserInteractor) :
    MvpPresenter<RegistrationView>(), RegistrationPresenterCallback {

    fun registerUser(name: String, surname: String, city: String, phone: String) {
        viewState.showLoading()
        val user = User(phone = phone, city = city, name = name, surname = surname)
        registrationUserInteractor.registerUser(user, this)
    }

    fun getMyPhoneNumber() = registrationUserInteractor.getMyPhoneNumber()

    override fun showSuccessfulRegistration(user: User) {
        viewState.goToProfile(user)
    }

    override fun registrationNameInputError() {
        viewState.hideLoading()
        viewState.setNameInputError("Допустимы только буквы и тире")
    }

    override fun registrationNameInputErrorEmpty() {
        viewState.hideLoading()
        viewState.setNameInputError("Введите своё имя")
    }

    override fun registrationNameInputErrorLong() {
        viewState.hideLoading()
        viewState.setNameInputError("Слишком длинное имя")
    }

    override fun registrationSurnameInputError() {
        viewState.hideLoading()
        viewState.setSurnameInputError("Допустимы только буквы и тире")
    }

    override fun registrationSurnameInputErrorEmpty() {
        viewState.hideLoading()
        viewState.setSurnameInputError("Введите свою фамилию")
    }

    override fun registrationSurnameInputErrorLong() {
        viewState.hideLoading()
        viewState.setSurnameInputError("Слишком длинная фамилия")
    }

    override fun registrationCityInputError() {
        viewState.hideLoading()
        viewState.showNoSelectedCity()
    }

}