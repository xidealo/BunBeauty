package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.BaseInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.iLogIn.IAuthorizationInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.db.dao.UserDao
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User
import com.bunbeauty.ideal.myapplication.helpApi.LoadingGuestServiceData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch


class AuthorizationInteractor(private val userDao: UserDao)  : BaseInteractor(), IAuthorizationInteractor{

    val TAG = "DBInf"

    val FIRST_ENTER_ID = "1"


    override fun getCurrentFbUser(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }

    //TODO add better checker
    @SuppressLint("CheckResult")
    override fun isPhoneCorrect(myPhoneNumber: String): Boolean {
        if (myPhoneNumber.length == 12) {

            launch {
                val user = User()
                user.id = FIRST_ENTER_ID
                user.phone = myPhoneNumber
                userDao.insert(user)
                Log.d(TAG, "user saved")
                Log.d(TAG, userDao.findById(FIRST_ENTER_ID).toString())
            }

            return true
        }
        return false
    }

    fun clearUsers() {
        launch {
            userDao.deleteAll()
        }
    }

}