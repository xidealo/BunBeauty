package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.iLogIn

import com.google.firebase.auth.FirebaseUser

interface IAuthorizationInteractor {


    fun getCurrentFbUser() : FirebaseUser?
    fun isPhoneCorrect(phone: String) : Boolean
}