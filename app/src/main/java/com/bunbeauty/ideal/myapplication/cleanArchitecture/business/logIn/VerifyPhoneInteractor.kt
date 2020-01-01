package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn

import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.iLogIn.IVerifyPhoneInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.IVerifyCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.user.IUserCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.BaseRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.UserRepository

class VerifyPhoneInteractor(private val userRepository: UserRepository,
                            private val intent: Intent) : BaseRepository(),
        IVerifyPhoneInteractor, IUserCallback {

    private val TAG = "DBInf"
    private val USER_PHONE = "user phone"

    lateinit var verifyCallback: IVerifyCallback

    override fun getMyPhoneNumber(): String = intent.getStringExtra(USER_PHONE)

    override fun getMyName(verifyCallback: IVerifyCallback) {
        this.verifyCallback = verifyCallback

        userRepository.getByPhoneNumber(getMyPhoneNumber(), this, true)
    }

    override fun returnUser(user: User) {
        verifyCallback.callbackGetUserName(user.name)
    }
}