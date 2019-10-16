package com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.interfaceRepositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Service

interface IServiceRepository {
    fun insert(service: Service, userId: String)
    fun delete(service: Service)
    fun update(service: Service)
    fun get(): List<Service>

    fun getAllUserServices(userId: String): List<Service>
}