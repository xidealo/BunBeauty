package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.RegistrationInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.RegistrationView

@InjectViewState
class RegistrationPresenter(private val registrationInteractor: RegistrationInteractor) :
        MvpPresenter<RegistrationView>() {

    fun registration(name: String, surname: String, city: String, phone:String) {

        if(isNameCorrect(name) && isSurnameCorrect(surname) && isCityCorrect(city)){
            val user = User()
            user.phone = phone
            user.name = "$name $surname"
            user.city = city

            registrationInteractor.registration(user)
            viewState.goToProfile()
        }
    }

    fun getMyPhoneNumber() = registrationInteractor.getMyPhoneNumber()

    private fun isNameCorrect(name: String): Boolean {
        if (name.isEmpty()) {
            viewState.setNameInputError("Введите своё имя")
            return false
        }

        if (!registrationInteractor.getIsNameInputCorrect(name)) {
            viewState.setNameInputError("Допустимы только буквы и тире")
            return false
        }

        if (!registrationInteractor.getIsNameLengthLessTwenty(name)) {
            viewState.setNameInputError("Слишком длинное имя")
            return false
        }
        return true
    }

    private fun isSurnameCorrect(surname: String): Boolean {
        if (surname.isEmpty()) {
            viewState.setSurnameInputError("Введите свою фамилию")
            return false
        }

        if (!registrationInteractor.getIsNameInputCorrect(surname)) {
            viewState.setSurnameInputError("Допустимы только буквы и тире")
            return false
        }

        if (!registrationInteractor.getIsNameLengthLessTwenty(surname)) {
            viewState.setSurnameInputError("Слишком длинная фамилия")
            return false
        }
        return true
    }

    private fun isCityCorrect(city: String): Boolean {
        if(!registrationInteractor.getIsCityInputCorrect(city)){
            viewState.showNoSelectedCity()
            return false
        }
        return true
    }


}