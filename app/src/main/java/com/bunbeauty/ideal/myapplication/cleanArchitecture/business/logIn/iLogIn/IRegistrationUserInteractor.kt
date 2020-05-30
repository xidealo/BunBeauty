package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.iLogIn

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.logIn.RegistrationPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

interface IRegistrationUserInteractor {
    fun registerUser(user: User, registrationPresenterCallback: RegistrationPresenterCallback)
    fun getIsCityInputCorrect(city: String) : Boolean
    fun getIsNameInputCorrect(name: String) : Boolean
    fun getIsNameLengthLessTwenty(name: String): Boolean
    fun getIsSurnameInputCorrect(surname: String): Boolean
    fun getIsSurnameLengthLessTwenty(surname: String): Boolean
    fun getMyPhoneNumber(): String
}