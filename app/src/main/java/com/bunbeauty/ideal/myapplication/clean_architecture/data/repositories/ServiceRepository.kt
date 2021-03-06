package com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.service.*
import com.bunbeauty.ideal.myapplication.clean_architecture.data.api.ServiceFirebase
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.dao.ServiceDao
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories.IServiceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ServiceRepository(
    private val serviceFirebase: ServiceFirebase
) : BaseRepository(), IServiceRepository {

    override fun insert(service: Service, insertServiceCallback: InsertServiceCallback) {
        launch {
            service.id = getIdForNew(service.userId)
            serviceFirebase.insert(service)
            withContext(Dispatchers.Main) {
                insertServiceCallback.returnCreatedCallback(service)
            }
        }
    }

    override fun delete(service: Service, deleteServiceCallback: DeleteServiceCallback) {
        launch {
            serviceFirebase.delete(service)
            withContext(Dispatchers.Main) {
                deleteServiceCallback.returnDeletedCallback(service)
            }
        }
    }

    override fun update(service: Service, updateServiceCallback: UpdateServiceCallback) {
        launch {
            serviceFirebase.update(service)
            withContext(Dispatchers.Main) {
                updateServiceCallback.returnUpdatedCallback(service)
            }
        }
    }

    override fun updatePremium(
        service: Service,
        durationPremium: Long,
        updateServiceCallback: UpdateServiceCallback
    ) {
        launch {
            withContext(Dispatchers.Main) {
                serviceFirebase.updatePremium(service, durationPremium, updateServiceCallback)
            }
        }
    }

    //Обратить внимание
    override fun get(getServicesCallback: GetServicesCallback) {
        launch {
            //val services = serviceDao.get()
            withContext(Dispatchers.Main) {
                //getServicesCallback.returnList(services)
            }
        }
    }

    override fun getById(
        serviceId: String,
        userId: String,
        isFirstEnter: Boolean,
        getServiceCallback: GetServiceCallback
    ) {

        if (isFirstEnter) {
            serviceFirebase.getById(userId, serviceId, getServiceCallback)
        } else {
            launch {
                //val services = serviceDao.getById(serviceId)
                withContext(Dispatchers.Main) {
                    //servicesCallback.returnServices(services)
                }
            }
        }
    }

    override fun getByUserId(
        userId: String,
        getServicesCallback: GetServicesCallback,
        isFirstEnter: Boolean
    ) {
        if (isFirstEnter) {
            serviceFirebase.getByUserId(userId, getServicesCallback)
        } else {
            launch {
                //val services = serviceDao.getAllByUserId(userId)
                withContext(Dispatchers.Main) {
                    //getServicesCallback.returnList(services)
                }
            }
        }
    }

    fun getIdForNew(userId: String): String = serviceFirebase.getIdForNew(userId)

    companion object {
        const val TAG = "DBInf"
    }
}