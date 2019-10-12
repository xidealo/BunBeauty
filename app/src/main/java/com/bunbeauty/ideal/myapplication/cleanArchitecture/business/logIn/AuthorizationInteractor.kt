package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.iLogIn.IAuthorizationInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.db.dao.UserDao
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.reactivex.schedulers.Schedulers

class AuthorizationInteractor(private val userDao: UserDao)  : IAuthorizationInteractor{


    val FIRST_ENTER_ID = "1"

    override fun getCurrentFbUser(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }

    //TODO add better checker
    override fun isPhoneCorrect(myPhoneNumber: String): Boolean {
        if (myPhoneNumber.length == 12) {

            val user = User()
            user.id = FIRST_ENTER_ID
            user.phone = myPhoneNumber
            userDao.insert(user = user).observeOn(Schedulers.newThread()).subscribe()

            return true
        }
        return false
    }
}