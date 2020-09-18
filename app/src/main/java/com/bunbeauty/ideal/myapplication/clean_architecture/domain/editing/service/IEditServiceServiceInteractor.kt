package com.bunbeauty.ideal.myapplication.clean_architecture.domain.editing.service

import android.content.Intent
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.service.EditServicePresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service

interface IEditServiceServiceInteractor {

    fun getGottenService(): Service

    fun getService(intent: Intent, editServicePresenterCallback: EditServicePresenterCallback)
    fun update(
        service: Service,
        editServicePresenterCallback: EditServicePresenterCallback
    )

    fun delete(
        service: Service,
        editServicePresenterCallback: EditServicePresenterCallback
    )
}