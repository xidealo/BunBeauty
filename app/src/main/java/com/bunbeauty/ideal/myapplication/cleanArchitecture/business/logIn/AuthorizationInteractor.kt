package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn

import android.annotation.SuppressLint
import android.util.Log
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.iLogIn.IAuthorizationInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.IUserSubscriber
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.BaseRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class AuthorizationInteractor(private val userRepository: UserRepository)  : BaseRepository(),
        IAuthorizationInteractor, IUserSubscriber{

    val TAG = "DBInf"
    val FIRST_ENTER_ID = "1"

    lateinit var userSubscriber: IUserSubscriber

    override fun getCurrentFbUser(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }

    //TODO add better checker
    @SuppressLint("CheckResult")
    override fun isPhoneCorrect(myPhoneNumber: String): Boolean {
        if (myPhoneNumber.length == 12) {
            launch {
                userRepository.deleteById(FIRST_ENTER_ID)
                val user = User()
                user.id = FIRST_ENTER_ID
                user.phone = myPhoneNumber
                userRepository.insert(user)
                Log.d(TAG, "user saved")
            }

            return true
        }
        return false
    }

    fun getUserName(userSubscriber: IUserSubscriber) {
        this.userSubscriber = userSubscriber

        val userPhone = FirebaseAuth.getInstance().currentUser!!.phoneNumber!!
        userRepository.getByPhoneNumber(userPhone, this)
    }

    override fun returnUser(user: User) = userSubscriber.returnUser(user)

}