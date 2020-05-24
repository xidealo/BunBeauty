package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.iLogIn

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.logIn.IRegistrationPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

interface IRegistrationInteractor {
    fun checkDataAndRegisterUser(user: User, iRegistrationPresenter: IRegistrationPresenter)
    fun getIsCityInputCorrect(city: String) : Boolean
    fun getIsNameInputCorrect(name: String) : Boolean
    fun getIsNameLengthLessTwenty(name: String): Boolean
    fun getIsSurnameInputCorrect(surname: String): Boolean
    fun getIsSurnameLengthLessTwenty(surname: String): Boolean
    fun getMyPhoneNumber(): String
}