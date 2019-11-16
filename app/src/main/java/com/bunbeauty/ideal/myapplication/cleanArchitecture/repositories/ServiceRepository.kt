package com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.IServiceSubscriber
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.ServiceFirebaseApi
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao.ServiceDao
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
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

    override fun update(service: Service) {
        launch {
            serviceDao.update(service)
        }
        serviceFirebaseApi.update(service)
    }

    override fun get() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(service: Service) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getServicesByUserId(userId: String, serviceSubscriber: IServiceSubscriber, isFirstEnter: Boolean) {
        this.serviceSubscriber = serviceSubscriber
        val serviceList: ArrayList<Service> = ArrayList()

        if (isFirstEnter) {
            serviceFirebaseApi.getServicesByUserId(userId, this)
        } else {
            runBlocking {
                serviceList.addAll(serviceDao.findAllByUserId(userId))
            }
            serviceSubscriber.returnServiceList(serviceList)
        }
    }

    override fun getServicesByUserIdAndServiceName(userId: String, serviceName:String, serviceSubscriber: IServiceSubscriber, isFirstEnter: Boolean) {
        this.serviceSubscriber = serviceSubscriber
        val serviceList: ArrayList<Service> = ArrayList()

        if (isFirstEnter) {
            serviceFirebaseApi.getServicesByUserIdAndServiceName(userId,serviceName, this)
        } else {
            runBlocking {
                serviceList.addAll(serviceDao.findAllByUserIdAndServiceName(userId,serviceName))
            }
            serviceSubscriber.returnServiceList(serviceList)
        }
    }

    fun getById(serviceId: String, userId: String, serviceSubscriber: IServiceSubscriber) {
        this.serviceSubscriber = serviceSubscriber
        var service: Service? = null

        runBlocking {
            service = serviceDao.findById(serviceId)
        }

        if (service == null) {
            serviceFirebaseApi.getById(userId, serviceId, this)
        } else {
            serviceSubscriber.returnService(service!!)
        }
    }

    fun getMaxCost(): Service{
        var service = Service()
        runBlocking {
            service = serviceDao.findMaxCostService()
        }
        return service
    }

    fun getMaxCountOfRates():Service{
        var service = Service()
        runBlocking {
            service = serviceDao.findMaxCountOfRatesService()
        }
        return service
    }

    //don't touch this methods just return value and don't have logic
    override fun returnService(service: Service) {
        // new method
        launch {
            serviceDao.insert(service)
        }

        serviceSubscriber.returnService(service)
    }

    override fun returnServiceList(serviceList: List<Service>) {
        //new method
        launch {
            for (service in serviceList) {
                serviceDao.insert(service)
            }
        }

        serviceSubscriber.returnServiceList(serviceList)
    }

    override fun getServicesByCityAndCategory(userCity: String, category: String, selectedTagsArray: java.util.ArrayList<String>?) {
    }

    fun getIdForNew(userId: String): String = serviceFirebaseApi.getIdForNew(userId)

    companion object{
        const val TAG = "DBInf"
    }

}