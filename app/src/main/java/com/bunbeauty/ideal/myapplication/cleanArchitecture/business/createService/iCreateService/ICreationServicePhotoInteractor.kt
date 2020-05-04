package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.createService.iCreateService

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service

interface ICreationServicePhotoInteractor {
    fun addImages(service: Service)
}