package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn

import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.iLogIn.IAuthorizationInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.logIn.IAuthorizationPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.user.IUserCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.BaseRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthorizationInteractor(private val userRepository: UserRepository,
                              private val intent: Intent) : BaseRepository(),
        IAuthorizationInteractor, IUserCallback {

    val TAG = "DBInf"
    private lateinit var authorizationPresenter: IAuthorizationPresenter

    fun authorize(authorizationPresenter: IAuthorizationPresenter) {
        this.authorizationPresenter = authorizationPresenter

        if (getCurrentFbUser() != null) {
            getUserByPhoneNumber(FirebaseAuth.getInstance().currentUser!!.phoneNumber!!)
        } else {
            authorizationPresenter.showViewOnScreen()
        }
    }

    fun authorize(phone: String, authorizationPresenter: IAuthorizationPresenter) {
        if (isPhoneCorrect(phone.trim())) {
            authorizationPresenter.goToVerifyPhone(phone)
        } else {
            authorizationPresenter.setPhoneError()
        }
    }

    override fun getCurrentFbUser(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }

    override fun isPhoneCorrect(phone: String): Boolean {
        return phone.length == 12
    }

    private fun getUserByPhoneNumber(userPhone: String) {
        userRepository.getByPhoneNumber(userPhone, this, true)
    }

    override fun returnUser(user: User) {
        if (user.name.isEmpty()) {
            authorizationPresenter.goToRegistration(user.phone)
        } else {
            authorizationPresenter.goToProfile()
        }
    }

}