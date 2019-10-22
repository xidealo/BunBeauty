package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.iLogIn

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.IUserSubscriber
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User

interface IRegistrationInteractor {
    fun getIsCityInputCorrect(city: String) : Boolean
    fun getIsNameInputCorrect(name: String) : Boolean
    fun getIsNameLengthLessTwenty(name: String): Boolean
    fun getIsSurnameInputCorrect(surname: String): Boolean
    fun getIsSurnameLengthLessTwenty(surname: String): Boolean
    fun getMyPhoneNumber(userSubscriber: IUserSubscriber)
    fun getUserId() : String
    fun registration(user: User)
}