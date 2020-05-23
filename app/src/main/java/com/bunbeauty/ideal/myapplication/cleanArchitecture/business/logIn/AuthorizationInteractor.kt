package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.iLogIn.IAuthorizationInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.logIn.AuthorizationPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.user.UsersCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.BaseRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.IUserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthorizationInteractor(private val userRepository: IUserRepository) : BaseRepository(),
    IAuthorizationInteractor, UsersCallback {

    private lateinit var authorizationPresenterCallback: AuthorizationPresenterCallback

    override fun defaultAuthorize(authorizationPresenterCallback: AuthorizationPresenterCallback) {
        this.authorizationPresenterCallback = authorizationPresenterCallback

        if (getCurrentFbUser() != null) {
            userRepository.getByPhoneNumber(
                FirebaseAuth.getInstance().currentUser!!.phoneNumber!!,
                this,
                true
            )
        } else {
            authorizationPresenterCallback.showViewOnScreen()
        }
    }

    override fun returnUsers(users: List<User>) {
        if (users.isNotEmpty() && users.first().name.isNotEmpty()) {
            authorizationPresenterCallback.goToProfile()
            return
        }

        if (FirebaseAuth.getInstance().currentUser == null) {
            authorizationPresenterCallback.showViewOnScreen()
            return
        } else {
            authorizationPresenterCallback.goToRegistration(getCurrentFbUser()!!.phoneNumber!!)
        }
    }

    override fun authorize(
        phone: String,
        authorizationPresenterCallback: AuthorizationPresenterCallback
    ) {
        if (isPhoneCorrect(phone.trim())) {
            authorizationPresenterCallback.goToVerifyPhone(phone)
        } else {
            authorizationPresenterCallback.setPhoneError()
        }
    }

    private fun getCurrentFbUser(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }

    fun isPhoneCorrect(phone: String): Boolean {
        return phone.length == 12
    }



}