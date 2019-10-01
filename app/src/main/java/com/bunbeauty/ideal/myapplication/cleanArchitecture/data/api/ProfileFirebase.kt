package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.ProfileInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.ProfileCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.FBListener
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.logIn.ProfileRepository
import com.bunbeauty.ideal.myapplication.helpApi.ListeningManager
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithStringsApi
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileFirebase(profileInteractor: ProfileInteractor) : ProfileRepository {

    private val profileCallback: ProfileCallback = profileInteractor

    override fun loadProfileData(ownerId: String) {
        val userReference = FirebaseDatabase.getInstance()
                .getReference(User.USERS)
                .child(ownerId)
        val userListener = userReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(userSnapshot: DataSnapshot) {

                /*LoadingProfileData.loadUserInfo(userSnapshot, database)

                LoadingProfileData.loadUserServices(userSnapshot.child(Service.SERVICES),
                        ownerId,
                        database)*/

                val name = WorkWithStringsApi.doubleCapitalSymbols(userSnapshot.child(User.NAME).getValue(String::class.java)!!)
                val city = WorkWithStringsApi.firstCapitalSymbol(userSnapshot.child(User.CITY).getValue(String::class.java)!!)
                val user = User()
                user.name = name
                user.city = city
                user.phone = userSnapshot.child(User.PHONE).getValue(String::class.java)!!
                user.countOfRates = userSnapshot.child(User.COUNT_OF_RATES).getValue(Long::class.java)!!
                user.rating = userSnapshot.child(User.AVG_RATING).getValue(Float::class.java)!!

                profileCallback.callbackSetProfile(user)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        ListeningManager.addToListenerList(FBListener(userReference, userListener))
    }

    override fun updateProfileData() {
    }

    override fun getCountOfRates(): Long {
        return 0
    }

}