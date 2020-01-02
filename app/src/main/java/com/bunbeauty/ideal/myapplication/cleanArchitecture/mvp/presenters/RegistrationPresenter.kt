package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.RegistrationInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.logIn.IRegistrationPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.RegistrationView

@InjectViewState
class RegistrationPresenter(private val registrationInteractor: RegistrationInteractor) :
        MvpPresenter<RegistrationView>(), IRegistrationPresenter {

    fun registration(name: String, surname: String, city: String, phone: String) {
        viewState.disableRegistrationButtnon()
        val user = User()
        user.phone = phone
        user.city = city
        registrationInteractor.registration(user, name,surname,this)
    }

    fun getMyPhoneNumber() = registrationInteractor.getMyPhoneNumber()

    override fun showSuccessfulRegistration() {
        viewState.showSuccessfulRegistration()
        viewState.goToProfile()
    }

    override fun registrationNameInputError() {
        viewState.enableRegistrationButtnon()
        viewState.setNameInputError("Допустимы только буквы и тире")
    }

    override fun registrationNameInputErrorEmpty() {
        viewState.enableRegistrationButtnon()
        viewState.setNameInputError("Введите своё имя")
    }

    override fun registrationNameInputErrorLong() {
        viewState.enableRegistrationButtnon()
        viewState.setNameInputError("Слишком длинное имя")
    }

    override fun registrationSurnameInputError() {
        viewState.enableRegistrationButtnon()
        viewState.setSurnameInputError("Допустимы только буквы и тире")
    }

    override fun registrationSurnameInputErrorEmpty() {
        viewState.enableRegistrationButtnon()
        viewState.setSurnameInputError("Введите свою фамилию")
    }

    override fun registrationSurnameInputErrorLong() {
        viewState.enableRegistrationButtnon()
        viewState.setSurnameInputError("Слишком длинная фамилия")
    }

    override fun registrationCityInputError() {
        viewState.enableRegistrationButtnon()
        viewState.showNoSelectedCity()
    }

}