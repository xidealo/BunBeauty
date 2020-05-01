package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service

import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service.iService.IServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.IPhotoCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.service.ServicePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.BaseRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.PhotoRepository
import com.google.firebase.auth.FirebaseAuth

class ServiceInteractor(private val photoRepository: PhotoRepository, private val intent: Intent) :
    BaseRepository(), IServiceInteractor, IPhotoCallback {

    private lateinit var photoCallback: IPhotoCallback
    private lateinit var user: User

    private lateinit var service: Service

    override fun createServiceScreen(servicePresenterCallback: ServicePresenterCallback) {
        val service = intent.getSerializableExtra(Service.SERVICE) as Service
        val user = intent.getSerializableExtra(User.USER) as User
        this.user = user
        this.service = service

        if (!isMyService()) {
            servicePresenterCallback.showPremium(service)
            servicePresenterCallback.createAlienServiceTopPanel(user, service)
        } else {
            servicePresenterCallback.createOwnServiceTopPanel(service)
        }

        servicePresenterCallback.showService(service)
    }

    override fun iconClick(servicePresenterCallback: ServicePresenterCallback) {
        if (isMyService()) {
            servicePresenterCallback.goToEditService(service)
        } else {
            servicePresenterCallback.goToProfile(user)
        }
    }

    private fun isMyService(): Boolean = getUserId() == user.id

    fun getServicePhotos(serviceId: String, serviceOwnerId: String, photoCallback: IPhotoCallback) {
        this.photoCallback = photoCallback

        photoRepository.getByServiceId(serviceId, serviceOwnerId, this, true)
    }

    override fun returnPhotos(photos: List<Photo>) {
        photoCallback.returnPhotos(photos)
    }

    private fun getUserId(): String = FirebaseAuth.getInstance().currentUser!!.uid

}