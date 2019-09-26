package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn

import android.content.Context
import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.presentation.logIn.VerifyPhoneActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthorizationInteractor {
    private val PHONE_NUMBER = "phone number"

    fun getCurrentFbUser(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }

    fun isPhoneCorrect(myPhoneNumber: String): Boolean {
        if (myPhoneNumber.length == 12) {
            return true
        }
        return false
    }

    fun goToVerifyPhone(myPhoneNumber: String, context: Context) {
        val intent = Intent(context, VerifyPhoneActivity::class.java)
        intent.putExtra(PHONE_NUMBER, myPhoneNumber)
        context.startActivity(intent)
    }
}