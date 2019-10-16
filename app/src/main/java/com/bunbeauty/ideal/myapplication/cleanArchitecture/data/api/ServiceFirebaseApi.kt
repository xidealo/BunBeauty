package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api

import android.util.Log
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.interfaceRepositories.IServiceRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.interfaceRepositories.IUserRepository
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class ServiceFirebaseApi: IServiceRepository {

    private val TAG = "data_layer"

    override fun insert(service: Service, userId: String) {
        val database = FirebaseDatabase.getInstance()
        val serviceRef = database
                .getReference(User.USERS)
                .child(userId)
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

    override fun get(): List<Service> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAllUserServices(userId: String): List<Service> {
        var serviceList: ArrayList<Service> = ArrayList()
        val database = FirebaseDatabase.getInstance()
        val servicesRef = database
                .getReference(User.USERS)
                .child(userId)
                .child(Service.SERVICES)

        //for ()

        return serviceList
    }

}
