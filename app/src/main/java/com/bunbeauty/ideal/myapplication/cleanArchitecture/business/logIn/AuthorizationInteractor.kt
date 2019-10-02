package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn

import android.content.Context
import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.iLogIn.IAuthorizationInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.ui.activities.logIn.VerifyPhoneActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthorizationInteractor : IAuthorizationInteractor{
    private val PHONE_NUMBER = "phone number"


    override fun getCurrentFbUser(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }

    override fun isPhoneCorrect(myPhoneNumber: String): Boolean {
        if (myPhoneNumber.length == 12) {
            return true
        }
        return false
    }

    override fun goToVerifyPhone(myPhoneNumber: String, context: Context) {
        val intent = Intent(context, VerifyPhoneActivity::class.java)
        intent.putExtra(PHONE_NUMBER, myPhoneNumber)
        context.startActivity(intent)
    }
}