package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.iLogIn

import android.content.Context
import com.google.firebase.auth.FirebaseUser

interface IAuthorizationInteractor {
    fun getCurrentFbUser() : FirebaseUser?
    fun isPhoneCorrect(myPhoneNumber: String) : Boolean
    fun goToVerifyPhone(myPhoneNumber: String, context: Context)
}