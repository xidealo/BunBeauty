package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.RegistrationFirebase
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.RegistrationLocalDatabase
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.db.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.logIn.RegistrationRepository
import com.bunbeauty.ideal.myapplication.other.Profile
import com.google.firebase.auth.FirebaseAuth

class RegistrationInteractor {

    //TODO UNIT TEST
    fun cityInputCorrect(city: String): Boolean {
        if (city == "Выбрать город") {
            return false
        }
        return true
    }

    fun nameInputCorrect(name: String): Boolean {
        if (!name.matches("[a-zA-ZА-Яа-я\\-]+".toRegex())) {
            return false
        }
        return true
    }

    fun nameLengthLessTwenty(name: String): Boolean {
        if (name.length > 20) {
            return false
        }
        return true
    }

    fun surnameInputCorrect(surname: String): Boolean {
        if (!surname.matches("[a-zA-ZА-Яа-я\\-]+".toRegex())) {
            return false
        }
        return true
    }

    fun surnameLengthLessTwenty(surname: String): Boolean {
        if (surname.length > 20) {
            return false
        }
        return true
    }

    fun registration(user: User, context: Context) {
        var registrationRepository: RegistrationRepository = RegistrationFirebase()
        registrationRepository.addUser(user)

        registrationRepository = RegistrationLocalDatabase(context)
        registrationRepository.addUser(user)
    }

    fun getMyPhoneNumber(intent: Intent): String {
        return intent.getStringExtra(User.PHONE)
    }

    fun getUserId():String {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun goToProfile(activity: Activity) {
        val intent = Intent(activity, Profile::class.java)
        activity.startActivity(intent)
        activity.finish()
    }
}