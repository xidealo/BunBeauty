package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api

import android.util.Log
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.db.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.logIn.RegistrationRepository
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class RegistrationFirebase: RegistrationRepository {
    private val TAG = "data_layer"

    override fun addUser(user: User) {
        Log.d(TAG, "addUser in firebase $user")
        addUserInFirebase(user)
    }

    private fun addUserInFirebase(user: User) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference(User.USERS).child(user.id)

        val items = HashMap<String, Any>()
        items[User.NAME] = user.name
        items[User.CITY] = user.city
        items[User.PHONE] = user.phone
        items[User.AVG_RATING] = user.rating
        items[User.COUNT_OF_RATES] = user.countOfRates
        items[User.PHOTO_LINK] = user.photoLink
        myRef.updateChildren(items)
        Log.d(TAG, "RegistrationFirebase completed ")
    }

}