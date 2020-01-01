package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn

import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.iLogIn.IRegistrationInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.registration.IRegistrationPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.user.IInsertUsersCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.UserRepository
import com.google.firebase.auth.FirebaseAuth

class RegistrationInteractor(private val userRepository: UserRepository,
                             private val intent: Intent) : IRegistrationInteractor, IInsertUsersCallback{

    private val USER_PHONE = "user phone"

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

    override fun getMyPhoneNumber(): String = intent.getStringExtra(USER_PHONE)

    override fun getUserId(): String = FirebaseAuth.getInstance().currentUser!!.uid
    private lateinit var iRegistrationPresenter: IRegistrationPresenter
    override fun registration(user: User, iRegistrationPresenter: IRegistrationPresenter) {
        user.id = getUserId()
        userRepository.insert(user, this)
        this.iRegistrationPresenter = iRegistrationPresenter
    }

    override fun returnInsertCallback(users: User) {
        iRegistrationPresenter.showSuccessfulRegistration()
    }

}