package com.bunbeauty.ideal.myapplication.clean_architecture.business.log_in

import com.bunbeauty.ideal.myapplication.clean_architecture.business.log_in.iLogIn.IAuthorizationInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.logIn.AuthorizationPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.user.UserCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.BaseRepository
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories.IUserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthorizationInteractor(private val userRepository: IUserRepository) : BaseRepository(),
    IAuthorizationInteractor, UserCallback {

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

    override fun returnGottenObject(element: User?) {
        if (element == null) {
            authorizationPresenterCallback.showViewOnScreen()
            return
        }

        if (element.name.isNotEmpty()) {
            authorizationPresenterCallback.goToProfile(element)
        }else {
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