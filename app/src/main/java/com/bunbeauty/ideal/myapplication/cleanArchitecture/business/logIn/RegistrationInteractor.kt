package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.iLogIn.IRegistrationInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.RegistrationFirebase
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.RegistrationLocalDatabase
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.logIn.RegistrationRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.ui.activities.profile.ProfileActivity
import com.google.firebase.auth.FirebaseAuth

class RegistrationInteractor : IRegistrationInteractor{

    override fun getIsCityInputCorrect(city: String): Boolean {
        if (city == "Выбрать город") {
            return false
        }
        return true
    }

    override fun getIsNameInputCorrect(name: String): Boolean {
        if (!name.matches("[a-zA-ZА-Яа-я\\-]+".toRegex())) {
            return false
        }
        return true
    }

    override fun getIsNameLengthLessTwenty(name: String): Boolean {
        if (name.length > 20) {
            return false
        }
        return true
    }

    override fun getIsSurnameInputCorrect(surname: String): Boolean {
        if (!surname.matches("[a-zA-ZА-Яа-я\\-]+".toRegex())) {
            return false
        }
        return true
    }

    override fun getIsSurnameLengthLessTwenty(surname: String): Boolean {
        if (surname.length > 20) {
            return false
        }
        return true
    }

    override fun getMyPhoneNumber(intent: Intent): String {
        return intent.getStringExtra(User.PHONE)
    }

    override fun getUserId():String {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    override fun registration(user: User, context: Context) {
        var registrationRepository: RegistrationRepository = RegistrationFirebase()
        registrationRepository.addUser(user)

        registrationRepository = RegistrationLocalDatabase(context)
        registrationRepository.addUser(user)
    }

    override fun goToProfile(activity: Activity) {
        val intent = Intent(activity, ProfileActivity::class.java)
        activity.startActivity(intent)
        activity.finish()
    }
}