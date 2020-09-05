package com.bunbeauty.ideal.myapplication.clean_architecture.data.api

import com.bunbeauty.ideal.myapplication.clean_architecture.business.api.server_time.ServerTimeCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.service.GetServiceCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.service.GetServicesCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Tag
import com.google.firebase.database.*
import com.google.firebase.functions.FirebaseFunctions
import java.util.*


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
        items[Service.DURATION] = service.duration
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
        items[Service.DURATION] = service.duration
        items[Service.COST] = service.cost
        items[Service.CATEGORY] = service.category
        items[Service.CREATION_DATE] = service.creationDate
        items[Service.PREMIUM_DATE] = service.premiumDate
        items[Service.AVG_RATING] = service.rating
        items[Service.COUNT_OF_RATES] = service.countOfRates
        serviceRef.updateChildren(items)
    }

    fun updatePremium(
        service: Service,
        durationPremium: Long
    ) {

        FirebaseFunctions.getInstance()
            .getHttpsCallable("getTime")
            .call()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val timestamp = task.result!!.data as Long
                    val serviceRef = FirebaseDatabase.getInstance()
                        .getReference(Service.SERVICES)
                        .child(service.userId)
                        .child(service.id)

                    val items = HashMap<String, Any>()
                    items[Service.NAME] = service.name
                    items[Service.ADDRESS] = service.address
                    items[Service.DESCRIPTION] = service.description
                    items[Service.DURATION] = service.duration
                    items[Service.COST] = service.cost
                    items[Service.CATEGORY] = service.category
                    items[Service.CREATION_DATE] = service.creationDate
                    items[Service.PREMIUM_DATE] = timestamp + durationPremium
                    items[Service.AVG_RATING] = service.rating
                    items[Service.COUNT_OF_RATES] = service.countOfRates
                    serviceRef.updateChildren(items)
                }
            }
    }

    fun getById(userId: String, serviceId: String, getServiceCallback: GetServiceCallback) {
        val serviceReference = FirebaseDatabase.getInstance()
            .getReference(Service.SERVICES)
            .child(userId)
            .child(serviceId)

        serviceReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(serviceSnapshot: DataSnapshot) {
                getServiceCallback.returnGottenObject(
                    getServiceFromSnapshot(serviceSnapshot, userId)
                )
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun getByUserId(userId: String, getServicesCallback: GetServicesCallback) {
        val servicesReference = FirebaseDatabase.getInstance()
            .getReference(Service.SERVICES)
            .child(userId)

        servicesReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(servicesSnapshot: DataSnapshot) {
                getServicesCallback.returnList(returnServiceList(servicesSnapshot, userId))
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
        val service = Service(
            id = serviceSnapshot.key!!,
            name = serviceSnapshot.child(Service.NAME).value as? String ?: "",
            address = serviceSnapshot.child(Service.ADDRESS).value as? String ?: "",
            description = serviceSnapshot.child(Service.DESCRIPTION).value as? String ?: "",
            duration = serviceSnapshot.child(Service.DURATION).getValue(Double::class.java)
                ?.toFloat() ?: 0.5f,
            cost = serviceSnapshot.child(Service.COST).getValue(Long::class.java) ?: 0,
            countOfRates = serviceSnapshot.child(Service.COUNT_OF_RATES).getValue(Long::class.java)
                ?: 0L,
            rating = serviceSnapshot.child(Service.AVG_RATING).getValue(Float::class.java) ?: 0f,
            category = serviceSnapshot.child(Service.CATEGORY).value as? String ?: "",
            creationDate = serviceSnapshot.child(Service.CREATION_DATE).value as? Long ?: 0,
            premiumDate = serviceSnapshot.child(Service.PREMIUM_DATE).value as? Long ?: 0,
            userId = userId
        )

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
