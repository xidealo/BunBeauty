package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api

import android.util.Log
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.IServiceSubscriber
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User
import com.google.firebase.database.*
import java.util.*

class ServiceFirebaseApi {

    private val TAG = "data_layer"

    fun insert(service: Service) {
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

    fun delete(service: Service) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun update(service: Service) {
        val serviceRef = FirebaseDatabase.getInstance()
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
    }

    fun get(): List<Service> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun getById(userId: String, serviceId: String, serviceSubscriber: IServiceSubscriber) {
        val servicesRef = FirebaseDatabase.getInstance()
                .getReference(User.USERS)
                .child(userId)
                .child(Service.SERVICES)
                .child(serviceId)

        servicesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(serviceSnapshot: DataSnapshot) {
                returnService(serviceSnapshot, userId, serviceSubscriber)
            }

            override fun onCancelled(error: DatabaseError) {
                // Some error
            }
        })
    }

    fun getServicesByUserId(userId: String, serviceSubscriber: IServiceSubscriber) {
        val servicesRef = FirebaseDatabase.getInstance()
                .getReference(User.USERS)
                .child(userId)
                .child(Service.SERVICES)

        servicesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(servicesSnapshot: DataSnapshot) {
                returnServiceList(servicesSnapshot, userId, serviceSubscriber)
                setListener(servicesRef, userId, serviceSubscriber)
            }

            override fun onCancelled(error: DatabaseError) {
                // Some error
            }
        })
    }

    private fun setListener(servicesRef: DatabaseReference, userId: String, serviceSubscriber: IServiceSubscriber) {
        servicesRef.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(serviceSnapshot: DataSnapshot, pChildName: String?) {
                returnService(serviceSnapshot, userId, serviceSubscriber)
            }

            override fun onChildChanged(serviceSnapshot: DataSnapshot, pChildName: String?) {
                returnService(serviceSnapshot, userId, serviceSubscriber)
            }

            override fun onChildRemoved(serviceSnapshot: DataSnapshot) {}

            override fun onChildMoved(serviceSnapshot: DataSnapshot, pChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun returnService(serviceSnapshot: DataSnapshot, userId: String, serviceSubscriber: IServiceSubscriber) {
        val service = getServiceFromSnapshot(serviceSnapshot, userId)

        serviceSubscriber.returnService(service)
    }

    private fun returnServiceList(servicesSnapshot: DataSnapshot, userId: String, serviceSubscriber: IServiceSubscriber) {
        val services = arrayListOf<Service>()
        for (serviceSnapshot in servicesSnapshot.children) {
            services.add(getServiceFromSnapshot(serviceSnapshot, userId))
        }
        serviceSubscriber.returnServiceList(services)
    }

    fun getIdForNew(userId: String): String {
        return FirebaseDatabase.getInstance().getReference(User.USERS)
                .child(userId)
                .child(Service.SERVICES).push().key!!
    }

    private fun getServiceFromSnapshot(serviceSnapshot: DataSnapshot, userId: String): Service {

        val service = Service()

        service.id = serviceSnapshot.key!!
        service.name = serviceSnapshot.child(Service.NAME).getValue<String>(String::class.java)!!
        service.address = serviceSnapshot.child(Service.ADDRESS).getValue<String>(String::class.java)!!
        service.description = serviceSnapshot.child(Service.DESCRIPTION).getValue<String>(String::class.java)!!
        service.cost = serviceSnapshot.child(Service.COST).getValue<String>(String::class.java)!!
        service.countOfRates = serviceSnapshot.child(Service.COUNT_OF_RATES).getValue<Long>(Long::class.java)!!
        service.rating = serviceSnapshot.child(Service.AVG_RATING).getValue<Float>(Float::class.java)!!
        service.category = serviceSnapshot.child(Service.CATEGORY).getValue<String>(String::class.java)!!
        service.creationDate = serviceSnapshot.child(Service.CREATION_DATE).getValue<String>(String::class.java)!!
        service.premiumDate = serviceSnapshot.child(Service.PREMIUM_DATE).getValue<String>(String::class.java)!!
        service.userId = userId

        return service
    }
}
