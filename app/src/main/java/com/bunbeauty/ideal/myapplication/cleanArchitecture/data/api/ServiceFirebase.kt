package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.service.ServicesCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Tag
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList

class ServiceFirebase {

    private val TAG = "data_layer"

    fun insert(service: Service) {
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

    fun delete(service: Service) {
        val serviceRef= FirebaseDatabase.getInstance()
            .getReference(User.USER)
            .child(service.userId)
            .child(Service.SERVICES)
            .child(service.id)

        val items = HashMap<String, Any>()
        items[Service.NAME] = service.name
        items[Service.ADDRESS] = service.address
        items[Service.DESCRIPTION] = service.description
        items[Service.COST] = service.cost
        serviceRef.updateChildren(items)
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

    fun getById(
        userId: String, serviceId: String, servicesCallback: ServicesCallback
    ) {
        val servicesRef = FirebaseDatabase.getInstance()
            .getReference(User.USERS)
            .child(userId)
            .child(Service.SERVICES)
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
            .getReference(User.USERS)
            .child(userId)
            .child(Service.SERVICES)

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
        return FirebaseDatabase.getInstance().getReference(User.USERS)
            .child(userId)
            .child(Service.SERVICES).push().key!!
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
        service.creationDate = serviceSnapshot.child(Service.CREATION_DATE).value as? String ?: ""
        service.premiumDate = serviceSnapshot.child(Service.PREMIUM_DATE).value as? String ?: ""
        service.userId = userId
        for (tagSnapshot in serviceSnapshot.child(Tag.TAGS).children) {
            service.tags.add(getTagFromSnapshot(tagSnapshot, service.id, userId))
        }
        return service
    }

    private fun getPhotosFromSnapshot(
        photosSnapshot: DataSnapshot,
        serviceId: String
    ): List<Photo> {
        val photos = ArrayList<Photo>()

        for (photoSnapshot in photosSnapshot.children) {
            val photo = Photo()
            photo.id = photosSnapshot.key!!
            photo.link = photosSnapshot.child(Photo.LINK).getValue<String>(String::class.java)!!
            photo.serviceId = serviceId
            photos.add(photo)
        }

        return photos
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
