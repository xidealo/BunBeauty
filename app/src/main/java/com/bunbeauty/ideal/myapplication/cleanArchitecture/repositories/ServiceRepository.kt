package com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.ServiceFirebaseApi
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao.ServiceDao
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.interfaceRepositories.IServiceRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ServiceRepository(private val serviceDao: ServiceDao,
                        private val serviceFirebaseApi: ServiceFirebaseApi) : BaseRepository(), IServiceRepository {

    override fun insert(service: Service) {
        launch {
            serviceDao.insert(service)
        }
        serviceFirebaseApi.insert(service)
    }

    override fun delete(service: Service) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun update(service: Service) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun get(): List<Service> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAllUserServices(userId: String): List<Service> {
        val services: ArrayList<Service>  = ArrayList()
        runBlocking {
            services.addAll(serviceDao.findAllByUserId(userId))
        }

        if (services.isEmpty()){
            services.addAll(serviceFirebaseApi.getAllUserServices(userId))
            launch {
                for (service in services) {
                    serviceDao.insert(service)
                }
            }
        }
        return services
    }

    fun getIdForNew(userId: String): String = serviceFirebaseApi.getIdForNew(userId)

}