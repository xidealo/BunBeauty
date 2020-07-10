package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.editing.service

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.service.EditServicePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service

interface IEditServiceServiceInteractor {

    fun getCacheService(): Service

    fun createEditServiceScreen(editServicePresenterCallback: EditServicePresenterCallback)
    fun save(
        service: Service,
        editServicePresenterCallback: EditServicePresenterCallback
    )

    fun delete(
        service: Service,
        editServicePresenterCallback: EditServicePresenterCallback
    )
}