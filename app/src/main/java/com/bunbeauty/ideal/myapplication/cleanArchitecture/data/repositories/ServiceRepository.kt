package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.service.DeleteServiceCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.service.InsertServiceCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.service.ServicesCallback
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
    IServiceRepository {
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
    override fun get(servicesCallback: ServicesCallback) {
        launch {
            val services = serviceDao.get()
            withContext(Dispatchers.Main) {
                servicesCallback.returnServices(services)
            }
        }
    }

    override fun getById(
        serviceId: String,
        userId: String,
        isFirstEnter: Boolean,
        servicesCallback : ServicesCallback
    ) {

        if (isFirstEnter) {
            serviceFirebase.getById(userId, serviceId, servicesCallback)
        } else {
            launch {
                val services = serviceDao.getById(serviceId)
                withContext(Dispatchers.Main) {
                    //servicesCallback.returnServices(services)
                }
            }
        }
    }

    override fun getServicesByUserId(
        userId: String,
        servicesCallback: ServicesCallback,
        isFirstEnter: Boolean
    ) {
        if (isFirstEnter) {
            serviceFirebase.getServicesByUserId(userId, servicesCallback)
        } else {
            launch {
                val services = serviceDao.getAllByUserId(userId)
                withContext(Dispatchers.Main) {
                    servicesCallback.returnServices(services)
                }
            }
        }
    }

    fun getIdForNew(userId: String): String = serviceFirebase.getIdForNew(userId)

    companion object {
        const val TAG = "DBInf"
    }
}