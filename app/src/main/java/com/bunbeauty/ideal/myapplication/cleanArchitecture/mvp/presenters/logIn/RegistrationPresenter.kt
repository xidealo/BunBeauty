package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.logIn

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.RegistrationInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.logIn.IRegistrationPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.logIn.RegistrationView

@InjectViewState
class RegistrationPresenter(private val registrationInteractor: RegistrationInteractor) :
    MvpPresenter<RegistrationView>(), IRegistrationPresenter {

    fun registration(name: String, surname: String, city: String, phone: String) {
        viewState.disableRegistrationButton()
        val user = User()
        user.phone = phone
        user.city = city
        user.name = name
        user.surname = surname
        registrationInteractor.registration(user, this)
    }

    fun getMyPhoneNumber() = registrationInteractor.getMyPhoneNumber()

    override fun showSuccessfulRegistration() {
        viewState.showSuccessfulRegistration()
        viewState.goToProfile()
    }

    override fun registrationNameInputError() {
        viewState.enableRegistrationButton()
        viewState.setNameInputError("Допустимы только буквы и тире")
    }

    override fun registrationNameInputErrorEmpty() {
        viewState.enableRegistrationButton()
        viewState.setNameInputError("Введите своё имя")
    }

    override fun registrationNameInputErrorLong() {
        viewState.enableRegistrationButton()
        viewState.setNameInputError("Слишком длинное имя")
    }

    override fun registrationSurnameInputError() {
        viewState.enableRegistrationButton()
        viewState.setSurnameInputError("Допустимы только буквы и тире")
    }

    override fun registrationSurnameInputErrorEmpty() {
        viewState.enableRegistrationButton()
        viewState.setSurnameInputError("Введите свою фамилию")
    }

    override fun registrationSurnameInputErrorLong() {
        viewState.enableRegistrationButton()
        viewState.setSurnameInputError("Слишком длинная фамилия")
    }

    override fun registrationCityInputError() {
        viewState.enableRegistrationButton()
        viewState.showNoSelectedCity()
    }

}