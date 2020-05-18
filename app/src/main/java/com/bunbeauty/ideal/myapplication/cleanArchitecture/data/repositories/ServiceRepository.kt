package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.service.DeleteServiceCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.service.IServicesCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.service.InsertServiceCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.service.UpdateServiceCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.ServiceFirebase
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao.ServiceDao
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.IServiceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ServiceRepository(
    private val serviceDao: ServiceDao,
    private val serviceFirebase: ServiceFirebase
) : BaseRepository(),
    IServiceRepository, IServicesCallback {

    private lateinit var iServicesCallback: IServicesCallback

    override fun insert(service: Service, insertServiceCallback: InsertServiceCallback) {
        launch {
            service.id = getIdForNew(service.userId)
            serviceFirebase.insert(service)
            //serviceDao.insert(service)
            withContext(Dispatchers.Main) {
                insertServiceCallback.returnCreatedCallback(service)
            }
        }
    }

    override fun delete(service: Service, deleteServiceCallback: DeleteServiceCallback) {
        launch {
            //serviceDao.delete(service)
            serviceFirebase.delete(service)
            withContext(Dispatchers.Main) {
                deleteServiceCallback.returnDeletedCallback(service)
            }
        }
    }

    override fun update(service: Service, updateServiceCallback: UpdateServiceCallback) {
        launch {
            //serviceDao.update(service)
            serviceFirebase.update(service)
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
        isFirstEnter: Boolean
    ) {

        if (isFirstEnter) {
            serviceFirebase.getById(userId, serviceId, this)
        } else {
            launch {
                val services = serviceDao.getById(serviceId)
                withContext(Dispatchers.Main) {
                    iServicesCallback.returnServices(services)
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
            serviceFirebase.getServicesByUserId(userId, this)
        } else {
            launch {
                val services = serviceDao.getAllByUserId(userId)
                withContext(Dispatchers.Main) {
                    iServicesCallback.returnServices(services)
                }
            }
        }
    }

    override fun returnServices(serviceList: List<Service>) {
        for (service in serviceList) {
            //insertInRoom(service)
        }
        iServicesCallback.returnServices(serviceList)
    }

    fun getIdForNew(userId: String): String = serviceFirebase.getIdForNew(userId)

    companion object {
        const val TAG = "DBInf"
    }
}