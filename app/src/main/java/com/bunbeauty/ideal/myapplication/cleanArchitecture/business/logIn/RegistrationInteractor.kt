package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn

import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.iLogIn.IRegistrationInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.logIn.IRegistrationPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.user.InsertUsersCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.IUserRepository
import com.google.firebase.auth.FirebaseAuth

class RegistrationInteractor(
    private val userRepository: IUserRepository,
    private val intent: Intent
) : IRegistrationInteractor, InsertUsersCallback {

    private lateinit var iRegistrationPresenter: IRegistrationPresenter

    override fun getMyPhoneNumber(): String = intent.getStringExtra(User.PHONE) ?: ""

    override fun checkDataAndRegisterUser(
        user: User,
        iRegistrationPresenter: IRegistrationPresenter
    ) {
        this.iRegistrationPresenter = iRegistrationPresenter
        if (isNameCorrect(user.name, iRegistrationPresenter)
            && isSurnameCorrect(user.surname, iRegistrationPresenter)
            && isCityCorrect(user.city, iRegistrationPresenter)
        ) {
            user.id = getUserId()
            userRepository.insert(user, this)
        }
    }

    private fun getUserId(): String = FirebaseAuth.getInstance().currentUser!!.uid

    //TODO UNIT TEST
    private fun isNameCorrect(
        name: String,
        iRegistrationPresenter: IRegistrationPresenter
    ): Boolean {
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

    private fun isSurnameCorrect(
        surname: String,
        iRegistrationPresenter: IRegistrationPresenter
    ): Boolean {
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

    private fun isCityCorrect(
        city: String,
        iRegistrationPresenter: IRegistrationPresenter
    ): Boolean {
        if (!getIsCityInputCorrect(city)) {
            iRegistrationPresenter.registrationCityInputError()
            return false
        }
        return true
    }

    override fun getIsCityInputCorrect(city: String): Boolean {
        return city != "Город"
    }

    override fun getIsNameInputCorrect(name: String): Boolean {
        return name.matches("[a-zA-ZА-Яа-я\\-]+".toRegex())
    }

    override fun getIsNameLengthLessTwenty(name: String): Boolean {
        return name.length <= 20
    }

    override fun getIsSurnameInputCorrect(surname: String): Boolean {
        return surname.matches("[a-zA-ZА-Яа-я\\-]+".toRegex())
    }

    override fun getIsSurnameLengthLessTwenty(surname: String): Boolean {
        return surname.length <= 20
    }

    override fun returnCreatedCallback(obj: User) {
        iRegistrationPresenter.showSuccessfulRegistration()
    }

}