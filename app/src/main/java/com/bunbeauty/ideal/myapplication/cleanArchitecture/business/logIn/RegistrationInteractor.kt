package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.iLogIn.IRegistrationInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.UserRepository
import com.google.firebase.auth.FirebaseAuth

class RegistrationInteractor(private val userRepository: UserRepository) : IRegistrationInteractor {

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

    override fun getMyPhoneNumber(): String = userRepository.getById("1").phone

    override fun getUserId(): String = FirebaseAuth.getInstance().currentUser!!.uid

    override fun registration(user: User) {
        userRepository.insert(user)
    }
}