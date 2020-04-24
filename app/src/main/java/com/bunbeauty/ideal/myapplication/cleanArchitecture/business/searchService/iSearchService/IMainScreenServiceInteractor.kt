package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService.iSearchService

interface IMainScreenServiceInteractor {
    fun getServicesByUserId(userId: String)
    fun getServicesByUserIdAndServiceName(userId: String, serviceName: String)
}