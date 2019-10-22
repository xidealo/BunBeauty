package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api

import android.util.Log
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.interfaceRepositories.IServiceRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList

class ServiceFirebaseApi: IServiceRepository {

    private val TAG = "data_layer"

    override fun insert(service: Service) {
        val database = FirebaseDatabase.getInstance()
        val serviceRef = database
                .getReference(User.USERS)
                .child(service.userId)
                .child(Service.SERVICES)
                .child(service.id)

        val items = HashMap<String, Any>()
        items[Service.NAME] = service.name
        items[Service.ADDRESS] = service.address
        items[Service.DESCRIPTION] = service.description
        items[Service.COST] = service.cost
        items[Service.CATEGORY] = service.category
        items[Service.CREATION_DATE] = service.creationDate
        items[Service.PREMIUM_DATE] = service.premiumDate
        items[Service.AVG_RATING] = service.rating
        items[Service.COUNT_OF_RATES] = service.countOfRates
        serviceRef.updateChildren(items)

        Log.d(TAG, "Service adding completed")
    }

    override fun delete(user: Service) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun update(user: Service) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun get() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAllUserServices(userId: String) {
        //val serviceList: ArrayList<Service> = ArrayList()
        val database = FirebaseDatabase.getInstance()
        val servicesRef = database
                .getReference(User.USERS)
                .child(userId)
                .child(Service.SERVICES)

        servicesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(servicesSnpshot: DataSnapshot) {

                val serviceList: ArrayList<Service> = ArrayList()
                for (serviceSnapshot in servicesSnpshot.children) {
                    val service = Service()

                    service.name = servicesSnpshot.child(Service.NAME).getValue<String>(String::class.java)!!
                    service.address = servicesSnpshot.child(Service.ADDRESS).getValue<String>(String::class.java)!!
                    service.description = servicesSnpshot.child(Service.DESCRIPTION).getValue<String>(String::class.java)!!
                    service.cost = servicesSnpshot.child(Service.COST).getValue<String>(String::class.java)!!
                    service.countOfRates = servicesSnpshot.child(Service.COUNT_OF_RATES).getValue<Long>(Long::class.java)!!
                    service.rating = servicesSnpshot.child(Service.AVG_RATING).getValue<Float>(Float::class.java)!!
                    service.category = servicesSnpshot.child(Service.CATEGORY).getValue<String>(String::class.java)!!
                    service.creationDate = servicesSnpshot.child(Service.CREATION_DATE).getValue<String>(String::class.java)!!
                    service.premiumDate = servicesSnpshot.child(Service.PREMIUM_DATE).getValue<String>(String::class.java)!!

                    serviceList.add(service)
                }
                val user = User()
            }

            override fun onCancelled(error: DatabaseError) {
                // Some error
            }
        })
    }

    fun getIdForNew(userId: String): String{
        return FirebaseDatabase.getInstance().getReference(User.USERS)
                .child(userId)
                .child(Service.SERVICES).push().key!!
    }
}
