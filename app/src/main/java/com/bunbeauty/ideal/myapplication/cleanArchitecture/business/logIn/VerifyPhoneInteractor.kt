package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn

import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.iLogIn.IVerifyPhoneInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.IUserSubscriber
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.VerifyCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.BaseRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.UserRepository

class VerifyPhoneInteractor(private val userRepository: UserRepository,
                            private val intent: Intent) : BaseRepository(),
        IVerifyPhoneInteractor, IUserSubscriber {

    private val TAG = "DBInf"
    private val USER_PHONE = "user phone"

    lateinit var verifyCallback: VerifyCallback

    override fun getMyPhoneNumber(): String = intent.getStringExtra(USER_PHONE)

    override fun getMyName(verifyCallback: VerifyCallback) {
        this.verifyCallback = verifyCallback

        userRepository.getByPhoneNumber(getMyPhoneNumber(), this)
    }

    /*override fun returnUser(user: User) {
        verifyCallback.callbackGetUserName(user.name)
    }*/

    override fun returnUserAdded(user: User) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}