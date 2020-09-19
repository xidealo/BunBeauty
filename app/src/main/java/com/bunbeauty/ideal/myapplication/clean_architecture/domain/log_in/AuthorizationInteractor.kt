package com.bunbeauty.ideal.myapplication.clean_architecture.domain.log_in

import com.bunbeauty.ideal.myapplication.clean_architecture.domain.log_in.iLogIn.IAuthorizationInteractor
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

    override fun authorizeByDefault(authorizationPresenterCallback: AuthorizationPresenterCallback) {
        this.authorizationPresenterCallback = authorizationPresenterCallback

        if (getCurrentUser() != null) {
            userRepository.getByPhoneNumber(
                FirebaseAuth.getInstance().currentUser!!.phoneNumber!!,
                this,
                true
            )
        } else {
            authorizationPresenterCallback.showDefaultAuthorizationFailed()
        }
    }

    override fun returnGottenObject(user: User?) {
        if (user == null) {
            authorizationPresenterCallback.showDefaultAuthorizationFailed()
            return
        }

        if (user.name.isNotEmpty()) {
            authorizationPresenterCallback.goToProfile(user)
        }else {
            authorizationPresenterCallback.goToRegistration(getCurrentUser()!!.phoneNumber!!)
        }
    }

    override fun authorize(
        code: String,
        phone: String,
        authorizationPresenterCallback: AuthorizationPresenterCallback
    ) {
        val fullPhone = code + phone
        if (isPhoneCorrect(fullPhone)) {
            authorizationPresenterCallback.goToVerifyPhone(fullPhone)
        } else {
            authorizationPresenterCallback.setPhoneError()
        }
    }

    private fun getCurrentUser(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }

    fun isPhoneCorrect(phone: String): Boolean {
        return phone.length == 12
    }
}