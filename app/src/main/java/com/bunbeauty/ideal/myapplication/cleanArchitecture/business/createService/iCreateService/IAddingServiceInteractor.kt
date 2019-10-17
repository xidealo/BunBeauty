package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.createService.iCreateService

import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Service

interface IAddingServiceInteractor {
    fun getUserId():String

    fun getIsNameInputCorrect(name: String) : Boolean
    fun getIsNameLengthLessTwenty(name: String): Boolean
    fun getIsDescriptionInputCorrect(description: String): Boolean
    fun getIsDescriptionLengthLessTwoHunded(description: String): Boolean
    fun getIsCostInputCorrect(cost: String): Boolean
    fun getIsCostLengthLessTen(cost: String): Boolean
    fun getIsCategoryInputCorrect(city: String) : Boolean
    fun getIsAddressInputCorrect(address: String): Boolean
    fun getIsAddressLengthThirty(address: String): Boolean

    fun addService(service:Service,  tags: List<String>)
}
