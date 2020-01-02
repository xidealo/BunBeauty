package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn

import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.iLogIn.IRegistrationInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.logIn.IRegistrationPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.user.IInsertUsersCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.UserRepository
import com.google.firebase.auth.FirebaseAuth

class RegistrationInteractor(private val userRepository: UserRepository,
                             private val intent: Intent) : IRegistrationInteractor, IInsertUsersCallback {

    private lateinit var iRegistrationPresenter: IRegistrationPresenter

    override fun getIsCityInputCorrect(city: String): Boolean {
        if (city == "Выбрать город") {
            return false
        }
        return true
    }

    override fun getIsNameInputCorrect(name: String): Boolean {
        if (!name.matches("[a-zA-ZА-Яа-я\\-]+".toRegex())) {
            return false
        }
        return true
    }

    override fun getIsNameLengthLessTwenty(name: String): Boolean {
        if (name.length > 20) {
            return false
        }
        return true
    }

    override fun getIsSurnameInputCorrect(surname: String): Boolean {
        if (!surname.matches("[a-zA-ZА-Яа-я\\-]+".toRegex())) {
            return false
        }
        return true
    }

    override fun getIsSurnameLengthLessTwenty(surname: String): Boolean {
        if (surname.length > 20) {
            return false
        }
        return true
    }

    override fun registration(user: User, name: String, surname: String, iRegistrationPresenter: IRegistrationPresenter) {
        this.iRegistrationPresenter = iRegistrationPresenter
        if (isNameCorrect(name, iRegistrationPresenter)
                && isSurnameCorrect(surname, iRegistrationPresenter)
                && isCityCorrect(user.city, iRegistrationPresenter)) {
            user.id = getUserId()
            user.name = "$name $surname"
            userRepository.insert(user, this)
        }
    }

    //TODO UNIT TEST
    private fun isNameCorrect(name: String, iRegistrationPresenter: IRegistrationPresenter): Boolean {
        if (name.isEmpty()) {
            iRegistrationPresenter.registrationNameInputErrorEmpty()
            return false
        }

        if (!getIsNameInputCorrect(name)) {
            iRegistrationPresenter.registrationNameInputError()
            return false
        }

        if (!getIsNameLengthLessTwenty(name)) {
            iRegistrationPresenter.registrationNameInputErrorLong()
            return false
        }
        return true
    }

    private fun isSurnameCorrect(surname: String, iRegistrationPresenter: IRegistrationPresenter): Boolean {
        if (surname.isEmpty()) {
            iRegistrationPresenter.registrationSurnameInputErrorEmpty()
            return false
        }

        if (!getIsNameInputCorrect(surname)) {
            iRegistrationPresenter.registrationSurnameInputError()
            return false
        }

        if (!getIsNameLengthLessTwenty(surname)) {
            iRegistrationPresenter.registrationSurnameInputErrorLong()
            return false
        }
        return true
    }

    private fun isCityCorrect(city: String, iRegistrationPresenter: IRegistrationPresenter): Boolean {
        if (!getIsCityInputCorrect(city)) {
            iRegistrationPresenter.registrationCityInputError()
            return false
        }
        return true
    }

    override fun getMyPhoneNumber(): String = intent.getStringExtra(USER_PHONE)!!
    override fun getUserId(): String = FirebaseAuth.getInstance().currentUser!!.uid

    override fun returnInsertCallback(users: User) {
        iRegistrationPresenter.showSuccessfulRegistration()
    }

    companion object {
        private const val USER_PHONE = "user phone"
    }

}