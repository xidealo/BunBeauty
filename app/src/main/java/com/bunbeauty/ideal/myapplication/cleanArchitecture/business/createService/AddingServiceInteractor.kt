package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.createService

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.createService.iCreateService.IAddingServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.ServiceRepository
import com.google.firebase.auth.FirebaseAuth

class AddingServiceInteractor(private val serviceRepository: ServiceRepository) : IAddingServiceInteractor{

    override fun addService(service: Service, tags: List<String>) {
        service.id = serviceRepository.getIdForNew(getUserId())
        serviceRepository.insert(service)
    }

    override fun getIsNameInputCorrect(name: String): Boolean {
        if (!name.matches("[a-zA-ZА-Яа-я\\- ]+".toRegex())) {
            return false
        }
        return true
    }

    override fun getIsNameLengthLessTwenty(name: String): Boolean = name.length < 20

    override fun getIsDescriptionInputCorrect(description: String): Boolean {
        // можно проверку на мат добавить
        return true
    }

    override fun getIsDescriptionLengthLessTwoHunded(description: String): Boolean = description.length < 200

    override fun getIsCostInputCorrect(cost: String): Boolean {
        if (!cost.matches("[\\d+]+".toRegex())) {
            return false
        }
        return true
    }

    override fun getIsCostLengthLessTen(cost: String): Boolean = cost.length < 10

    override fun getIsCategoryInputCorrect(city: String): Boolean {

        if (city == "выбрать категорию") {
            return false
        }
        return true
    }

    override fun getIsAddressInputCorrect(address: String): Boolean = true

    override fun getIsAddressLengthThirty(address: String): Boolean = address.length < 30

    override fun getUserId(): String = FirebaseAuth.getInstance().currentUser!!.uid

}