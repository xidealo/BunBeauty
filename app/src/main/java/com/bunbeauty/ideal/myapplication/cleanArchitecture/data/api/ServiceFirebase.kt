package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api

import android.util.Log
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.service.IServiceCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.service.IServicesCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Tag
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

class ServiceFirebase {

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

    fun getById(userId: String, serviceId: String, serviceSubscriber: IServiceCallback) {
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

    fun getServicesByUserId(
        userId: String,
        iServiceCallback: IServiceCallback,
        iServicesCallback: IServicesCallback
    ) {
        val servicesRef = FirebaseDatabase.getInstance()
            .getReference(User.USERS)
            .child(userId)
            .child(Service.SERVICES)

        servicesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(servicesSnapshot: DataSnapshot) {
                iServicesCallback.returnServices(returnServiceList(servicesSnapshot, userId))
                setListener(servicesRef, userId, iServiceCallback)
            }

            override fun onCancelled(error: DatabaseError) {
                // Some error
            }
        })
    }

    fun getServicesByUserIdAndServiceName(
        userId: String,
        serviceName: String,
        iServicesCallback: IServicesCallback,
        iServiceCallback: IServiceCallback
    ) {
        val servicesRef = FirebaseDatabase.getInstance()
            .getReference(User.USERS)
            .child(userId)
            .child(Service.SERVICES)

        servicesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(servicesSnapshot: DataSnapshot) {
                val services = arrayListOf<Service>()

                for (serviceSnapshot in servicesSnapshot.children) {
                    if (serviceName == serviceSnapshot.child(Service.NAME).value as? String ?: "")
                        services.add(getServiceFromSnapshot(serviceSnapshot, userId))
                }

                iServicesCallback.returnServices(services)

                setListener(servicesRef, userId, iServiceCallback)
            }

            override fun onCancelled(error: DatabaseError) {
                // Some error
            }
        })
    }

    private fun setListener(
        servicesRef: DatabaseReference,
        userId: String,
        iServiceCallback: IServiceCallback
    ) {
        servicesRef.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(serviceSnapshot: DataSnapshot, pChildName: String?) {
                returnService(serviceSnapshot, userId, iServiceCallback)
            }

            override fun onChildChanged(serviceSnapshot: DataSnapshot, pChildName: String?) {
                returnService(serviceSnapshot, userId, iServiceCallback)
            }

            override fun onChildRemoved(serviceSnapshot: DataSnapshot) {}

            override fun onChildMoved(serviceSnapshot: DataSnapshot, pChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun returnService(
        serviceSnapshot: DataSnapshot,
        userId: String,
        iServiceCallback: IServiceCallback
    ) {
        val service = getServiceFromSnapshot(serviceSnapshot, userId)

        iServiceCallback.returnService(service)
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
        //add default value
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
