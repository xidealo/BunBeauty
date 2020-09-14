package com.bunbeauty.ideal.myapplication.clean_architecture.domain.create_service.iCreateService

import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service

interface ICreationServiceTagInteractor {
    fun addTags(service: Service)
}