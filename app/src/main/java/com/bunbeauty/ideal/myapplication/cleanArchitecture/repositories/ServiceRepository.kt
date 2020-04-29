package com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.service.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.ServiceFirebaseApi
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao.ServiceDao
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.interfaceRepositories.IServiceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class ServiceRepository(
    private val serviceDao: ServiceDao,
    private val serviceFirebaseApi: ServiceFirebaseApi
) : BaseRepository(),
    IServiceRepository, IServiceCallback, IServicesCallback {

    private lateinit var iServiceCallback: IServiceCallback
    private lateinit var iServicesCallback: IServicesCallback

    override fun insert(service: Service, insertServiceCallback: InsertServiceCallback) {
        launch {
            serviceDao.insert(service)
            serviceFirebaseApi.insert(service)
            withContext(Dispatchers.Main) {
                insertServiceCallback.returnCreatedCallback(service)
            }
        }
    }

    override fun delete(service: Service, deleteServiceCallback: DeleteServiceCallback) {
        launch {
            serviceDao.delete(service)
            serviceFirebaseApi.delete(service)
            withContext(Dispatchers.Main) {
                deleteServiceCallback.returnDeletedCallback()
            }
        }
    }

    override fun update(service: Service, updateServiceCallback: UpdateServiceCallback) {
        launch {
            serviceDao.update(service)
            serviceFirebaseApi.update(service)
            withContext(Dispatchers.Main) {
                updateServiceCallback.returnUpdatedCallback(service)
            }
        }
    }

    //Обратить внимание
    override fun get(iServicesCallback: IServicesCallback) {
        launch {
            val services = serviceDao.get()
            withContext(Dispatchers.Main) {
                iServicesCallback.returnServices(services)
            }
        }
    }

    override fun getById(
        serviceId: String,
        userId: String,
        iServiceCallback: IServiceCallback,
        isFirstEnter: Boolean
    ) {
        this.iServiceCallback = iServiceCallback

        if (isFirstEnter) {
            serviceFirebaseApi.getById(userId, serviceId, this)
        } else {
            launch {
                val services = serviceDao.getById(serviceId)
                withContext(Dispatchers.Main) {
                    iServiceCallback.returnService(services)
                }
            }
        }
    }

    override fun getServicesByUserId(
        userId: String,
        iServicesCallback: IServicesCallback,
        isFirstEnter: Boolean
    ) {
        this.iServicesCallback = iServicesCallback
        if (isFirstEnter) {
            serviceFirebaseApi.getServicesByUserId(userId, this, this)
        } else {
            launch {
                val services = serviceDao.getAllByUserId(userId)
                withContext(Dispatchers.Main) {
                    iServicesCallback.returnServices(services)
                }
            }
        }
    }

    override fun getServicesByUserIdAndServiceName(
        userId: String, serviceName: String, iServicesCallback: IServicesCallback,
        isFirstEnter: Boolean
    ) {
        this.iServicesCallback = iServicesCallback
        if (isFirstEnter) {
            serviceFirebaseApi.getServicesByUserIdAndServiceName(userId, serviceName, this, this)
        } else {
            launch {
                val services = serviceDao.getAllByUserIdAndServiceName(
                    userId,
                    serviceName
                )
                withContext(Dispatchers.Main) {
                    iServicesCallback.returnServices(
                        services
                    )
                }
            }
        }
    }

    override fun returnService(service: Service) {
        insertInRoom(service)
        //iServiceCallback.returnService(service)
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

    fun getIdForNew(userId: String): String = serviceFirebaseApi.getIdForNew(userId)

    companion object {
        const val TAG = "DBInf"
    }

}