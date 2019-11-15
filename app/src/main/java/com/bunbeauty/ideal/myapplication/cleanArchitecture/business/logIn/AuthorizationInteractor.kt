package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn

import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.iLogIn.IAuthorizationInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.AuthorizationCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.IUserSubscriber
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.BaseRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthorizationInteractor(private val userRepository: UserRepository,
                              private val intent: Intent)  : BaseRepository(),
        IAuthorizationInteractor, IUserSubscriber{

    val TAG = "DBInf"
    //val USER_PHONE = "user phone"

    private lateinit var authorizationCallback: AuthorizationCallback

    override fun getCurrentFbUser(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }

    override fun isPhoneCorrect(phone: String): Boolean {
        return phone.length == 12
    }

    fun getUserName(authorizationCallback: AuthorizationCallback) {
        this.authorizationCallback = authorizationCallback

        val userPhone = FirebaseAuth.getInstance().currentUser!!.phoneNumber!!
        userRepository.getByPhoneNumber(userPhone, this, true)
    }

    override fun returnUser(user: User) {
        if (user.name.isEmpty()) {
            authorizationCallback.goToRegistration(user.phone)
        } else {
            authorizationCallback.goToProfile()
        }
    }

    override fun returnUsers(users: List<User>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}