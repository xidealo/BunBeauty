package com.bunbeauty.ideal.myapplication.clean_architecture.business.editing.service

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.service.EditServicePresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service

interface IEditServiceServiceInteractor {

    fun getCacheService(): Service

    fun createEditServiceScreen(editServicePresenterCallback: EditServicePresenterCallback)
    fun update(
        service: Service,
        editServicePresenterCallback: EditServicePresenterCallback
    )

    fun delete(
        service: Service,
        editServicePresenterCallback: EditServicePresenterCallback
    )
}