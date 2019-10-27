package com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.IServiceSubscriber
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.ServiceFirebaseApi
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao.ServiceDao
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.interfaceRepositories.IServiceRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ServiceRepository(private val serviceDao: ServiceDao,
                        private val serviceFirebaseApi: ServiceFirebaseApi) : BaseRepository(),
        IServiceRepository, IServiceSubscriber {

    private lateinit var serviceSubscriber: IServiceSubscriber

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
        launch {
            serviceDao.update(service)
        }
        serviceFirebaseApi.update(service)

    }

    override fun get() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAllUserServices(userId: String, serviceSubscriber: IServiceSubscriber) {
        this.serviceSubscriber = serviceSubscriber
        val serviceList: ArrayList<Service> = ArrayList()

        runBlocking {
            serviceList.addAll(serviceDao.findAllByUserId(userId))
        }

        if (serviceList.isEmpty()) {
            serviceFirebaseApi.getAllUserServices(userId, this)
        } else {
            serviceSubscriber.returnServiceList(serviceList)
        }
    }

    override fun returnServiceList(serviceList: List<Service>) {
        serviceSubscriber.returnServiceList(serviceList)
        launch {
            for (service in serviceList) {
                serviceDao.insert(service)
            }
        }
    }

    fun getIdForNew(userId: String): String = serviceFirebaseApi.getIdForNew(userId)

}