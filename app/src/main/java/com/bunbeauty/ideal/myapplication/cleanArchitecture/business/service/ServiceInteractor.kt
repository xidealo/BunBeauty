package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service

import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.iProfile.IServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.IPhotoCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.BaseRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.PhotoRepository
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithTimeApi

class ServiceInteractor(private val photoRepository: PhotoRepository, private val intent: Intent) :
        BaseRepository(), IServiceInteractor, IPhotoCallback {

    private lateinit var photoCallback: IPhotoCallback

    fun getService() = intent.extras?.get(Service.SERVICE) as Service

    fun getServiceOwner() = intent.extras?.get(Service.SERVICE_OWNER) as User

    fun isPremium(premiumDate: String): Boolean = WorkWithTimeApi.checkPremium(premiumDate)

    fun getServicePhotos(serviceId: String, serviceOwnerId: String, photoCallback: IPhotoCallback) {
        this.photoCallback = photoCallback

        photoRepository.getByServiceId(serviceId, serviceOwnerId, this, true)
    }

    override fun returnPhotos(photos: List<Photo>) {
        photoCallback.returnPhotos(photos)
    }
}