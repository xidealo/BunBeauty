package com.bunbeauty.ideal.myapplication.clean_architecture.domain.create_service.iCreateService

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.creation_service.CreationServicePresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service

interface ICreationServiceServiceInteractor {
    fun getIsNameInputCorrect(name: String): Boolean
    fun getIsNameLengthLessTwenty(name: String): Boolean
    fun getIsDescriptionInputCorrect(description: String): Boolean
    fun getIsDescriptionLengthLessTwoHundred(description: String): Boolean
    fun getIsCostInputCorrect(cost: String): Boolean
    fun getIsCostLengthLessTen(cost: String): Boolean
    fun getIsCategoryInputCorrect(category: String): Boolean
    fun getIsAddressInputCorrect(address: String): Boolean
    fun getIsAddressLengthThirty(address: String): Boolean

    fun addService(
        service: Service,
        creationServicePresenterCallback: CreationServicePresenterCallback
    )

}
