package com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.service.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.ServiceFirebaseApi
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao.ServiceDao
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.interfaceRepositories.IServiceRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ServiceRepository(private val serviceDao: ServiceDao,
                        private val serviceFirebaseApi: ServiceFirebaseApi) : BaseRepository(),
        IServiceRepository, IServiceCallback, IServicesCallback {

    private lateinit var iServiceCallback: IServiceCallback
    private lateinit var iServicesCallback: IServicesCallback

    override fun insert(service: Service, iInsertServiceCallback: IInsertServiceCallback) {
        launch {
            serviceDao.insert(service)
            serviceFirebaseApi.insert(service)
        }
    }

    override fun delete(service: Service, iDeleteServiceCallback: IDeleteServiceCallback) {
        launch {
            serviceDao.delete(service)
            serviceFirebaseApi.delete(service)
        }
    }

    override fun update(service: Service, iUpdateServiceCallback: IUpdateServiceCallback) {
        launch {
            serviceDao.update(service)
            serviceFirebaseApi.update(service)
        }
    }

    //Обратить внимание
    override fun get(iServicesCallback: IServicesCallback) {
        launch {
            iServicesCallback.returnServices(serviceDao.get())
        }
    }

    override fun getById(serviceId: String, userId: String, iServiceCallback: IServiceCallback, isFirstEnter: Boolean) {
        this.iServiceCallback = iServiceCallback

        if (isFirstEnter) {
            serviceFirebaseApi.getById(userId, serviceId, this)
        } else {
            launch {
                iServiceCallback.returnService(serviceDao.getById(serviceId))
            }
        }
    }

    override fun getServicesByUserId(userId: String, iServicesCallback: IServicesCallback, isFirstEnter: Boolean) {
        this.iServicesCallback = iServicesCallback
        if (isFirstEnter) {
            serviceFirebaseApi.getServicesByUserId(userId, this, this)
        } else {
            launch {
                iServicesCallback.returnServices(serviceDao.getAllByUserId(userId))
            }
        }
    }

    override fun getServicesByUserIdAndServiceName(userId: String, serviceName: String, iServicesCallback: IServicesCallback,
                                                   isFirstEnter: Boolean) {
        this.iServicesCallback = iServicesCallback
        if (isFirstEnter) {
            serviceFirebaseApi.getServicesByUserIdAndServiceName(userId, serviceName, this, this)
        } else {
            launch {
                iServicesCallback.returnServices(serviceDao.getAllByUserIdAndServiceName(userId, serviceName))
            }
        }
    }

    fun getMaxCost(): Service {
        var service = Service()
        runBlocking {
            service = serviceDao.getMaxCostService()
        }
        return service
    }

    fun getMaxCountOfRates(): Service {
        var service = Service()
        runBlocking {
            service = serviceDao.getMaxCountOfRatesService()
        }
        return service
    }

    override fun returnService(service: Service) {
        insertInRoom(service)
        iServiceCallback.returnService(service)
    }

    override fun returnServices(serviceList: List<Service>) {
        for (service in serviceList) {
            insertInRoom(service)
        }
        iServicesCallback.returnServices(serviceList)
    }

    override fun insertInRoom(service: Service) {
        launch {
            serviceDao.insert(service)
        }
    }

    override fun getServicesByCityAndCategory(userCity: String, category: String, selectedTagsArray: ArrayList<String>) {}

    fun getIdForNew(userId: String): String = serviceFirebaseApi.getIdForNew(userId)

    companion object {
        const val TAG = "DBInf"
    }

}