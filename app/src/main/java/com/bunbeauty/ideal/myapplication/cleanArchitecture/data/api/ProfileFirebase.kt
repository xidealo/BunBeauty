package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.ProfileInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.ProfileCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.FBListener
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Tag
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User
import com.bunbeauty.ideal.myapplication.helpApi.ListeningManager
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithStringsApi
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class ProfileFirebase(profileInteractor: ProfileInteractor){

    //TODO LOGS!
    private val profileCallback: ProfileCallback = profileInteractor

    fun loadProfileData(ownerId: String) {
        val userReference = FirebaseDatabase.getInstance()
                .getReference(User.USERS)
                .child(ownerId)
        val userListener = userReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(userSnapshot: DataSnapshot) {

                val name = WorkWithStringsApi.doubleCapitalSymbols(userSnapshot.child(User.NAME).getValue(String::class.java)!!)
                val city = WorkWithStringsApi.firstCapitalSymbol(userSnapshot.child(User.CITY).getValue(String::class.java)!!)
                val user = User()
                user.name = name
                user.city = city
                user.phone = userSnapshot.child(User.PHONE).getValue(String::class.java)!!
                user.countOfRates = userSnapshot.child(User.COUNT_OF_RATES).getValue(Long::class.java)!!
                user.rating = userSnapshot.child(User.AVG_RATING).getValue(Float::class.java)!!

                profileCallback.callbackGetProfileData(user)

                for (serviceSnap in userSnapshot.child(Service.SERVICES).children) {

                    val service = Service()
                    service.id = serviceSnap.key!!
                    service.name = serviceSnap.child(Service.NAME).getValue(String::class.java)!!
                    service.userId = userSnapshot.key!!
                    service.rating = serviceSnap.child(Service.AVG_RATING).getValue(Float::class.java)!!
                    val tagsArray = ArrayList<String>()
                    for (tag in serviceSnap.child(Tag.TAGS).children) {
                        tagsArray.add(tag.getValue(String::class.java)!!)
                    }
                    //service.tags = tagsArray
                    profileCallback.callbackGetService(service)

                    /*addUserServicesInLocalStorage(service, database)*/
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        ListeningManager.addToListenerList(FBListener(userReference, userListener))
    }

}