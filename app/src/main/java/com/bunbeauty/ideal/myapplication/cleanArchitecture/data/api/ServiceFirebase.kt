package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.service.ServicesCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Tag
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

class ServiceFirebase {

    fun insert(service: Service) {
        val serviceRef = FirebaseDatabase.getInstance()
            .getReference(Service.SERVICES)
            .child(service.userId)
            .child(service.id)

        val items = HashMap<String, Any>()
        items[Service.NAME] = service.name
        items[Service.ADDRESS] = service.address
        items[Service.DESCRIPTION] = service.description
        items[Service.COST] = service.cost
        items[Service.CATEGORY] = service.category
        items[Service.CREATION_DATE] = ServerValue.TIMESTAMP
        items[Service.PREMIUM_DATE] = 0
        items[Service.AVG_RATING] = service.rating
        items[Service.COUNT_OF_RATES] = service.countOfRates
        serviceRef.updateChildren(items)
    }

    fun delete(service: Service) {
        val serviceRef = FirebaseDatabase.getInstance()
            .getReference(Service.SERVICES)
            .child(service.userId)
            .child(service.id)

        serviceRef.removeValue()
    }

    fun update(service: Service) {
        val serviceRef = FirebaseDatabase.getInstance()
            .getReference(Service.SERVICES)
            .child(service.userId)
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

    fun getById(
        userId: String, serviceId: String, servicesCallback: ServicesCallback
    ) {
        val servicesRef = FirebaseDatabase.getInstance()
            .getReference(Service.SERVICES)
            .child(userId)
            .child(serviceId)

        servicesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(servicesSnapshot: DataSnapshot) {
                servicesCallback.returnServices(returnServiceList(servicesSnapshot, userId))
            }

            override fun onCancelled(error: DatabaseError) {
                // Some error
            }
        })
    }

    fun getServicesByUserId(
        userId: String,
        servicesCallback: ServicesCallback
    ) {
        val servicesRef = FirebaseDatabase.getInstance()
            .getReference(Service.SERVICES)
            .child(userId)

        servicesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(servicesSnapshot: DataSnapshot) {
                servicesCallback.returnServices(returnServiceList(servicesSnapshot, userId))
                //setListener(servicesRef, userId, iServiceCallback)
            }

            override fun onCancelled(error: DatabaseError) {
                // Some error
            }
        })
    }

    private fun returnServiceList(
        servicesSnapshot: DataSnapshot,
        userId: String
    ): ArrayList<Service> {
        val services = arrayListOf<Service>()
        for (serviceSnapshot in servicesSnapshot.children) {
            services.add(getServiceFromSnapshot(serviceSnapshot, userId))
        }
        return services
    }

    fun getIdForNew(userId: String): String {
        return FirebaseDatabase.getInstance()
            .getReference(Service.SERVICES)
            .child(userId)
            .push().key!!
    }

    private fun getServiceFromSnapshot(serviceSnapshot: DataSnapshot, userId: String): Service {
        val service = Service()
        service.id = serviceSnapshot.key!!
        service.name = serviceSnapshot.child(Service.NAME).value as? String ?: ""
        service.address = serviceSnapshot.child(Service.ADDRESS).value as? String ?: ""
        service.description = serviceSnapshot.child(Service.DESCRIPTION).value as? String ?: ""
        service.cost = serviceSnapshot.child(Service.COST).getValue(Long::class.java) ?: 0
        service.countOfRates =
            serviceSnapshot.child(Service.COUNT_OF_RATES).getValue(Long::class.java)
                ?: 0L
        service.rating = serviceSnapshot.child(Service.AVG_RATING).getValue(Float::class.java)
            ?: 0f
        service.category = serviceSnapshot.child(Service.CATEGORY).value as? String ?: ""
        service.creationDate = serviceSnapshot.child(Service.CREATION_DATE).value as? Long ?: 0
        service.premiumDate = serviceSnapshot.child(Service.PREMIUM_DATE).value as? Long ?: 0
        service.userId = userId
        for (tagSnapshot in serviceSnapshot.child(Tag.TAGS).children) {
            service.tags.add(getTagFromSnapshot(tagSnapshot, service.id, userId))
        }

        return service
    }

    private fun getTagFromSnapshot(
        tagSnapshot: DataSnapshot,
        serviceId: String,
        userId: String
    ): Tag {
        val tag = Tag()
        tag.id = tagSnapshot.key!!
        tag.tag = tagSnapshot.child(Tag.TAG).value as? String ?: ""
        tag.serviceId = serviceId
        tag.userId = userId
        return tag
    }
}
