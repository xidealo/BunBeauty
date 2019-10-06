package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.iLogIn.IAuthorizationInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerRoomComponent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.RoomModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.db.repo.UserRepo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class AuthorizationInteractor : IAuthorizationInteractor{

    /*@Inject
    lateinit var userRepo: UserRepo*/

    override fun getCurrentFbUser(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }

    //TODO add better checker
    override fun isPhoneCorrect(myPhoneNumber: String): Boolean {
        if (myPhoneNumber.length == 12) {
            return true
        }
        return false
    }


}