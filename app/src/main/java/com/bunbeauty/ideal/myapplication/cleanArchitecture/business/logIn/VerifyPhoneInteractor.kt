package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.iLogIn.IVerifyPhoneInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.IUserSubscriber
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.VerifyCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.BaseRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.UserRepository

class VerifyPhoneInteractor(private val userRepository: UserRepository) : BaseRepository(),
        IVerifyPhoneInteractor, IUserSubscriber {

    private val TAG = "DBInf"

    lateinit var verifyCallback: VerifyCallback

    override fun getMyPhoneNumber(verifyCallback: VerifyCallback) {
        this.verifyCallback = verifyCallback

        userRepository.getById("1", this)
    }

    override fun returnUser(user: User) {
        verifyCallback.callbackGetUserPhone(user.phone)
        verifyCallback.callbackGetUserName(user.name)
    }

}