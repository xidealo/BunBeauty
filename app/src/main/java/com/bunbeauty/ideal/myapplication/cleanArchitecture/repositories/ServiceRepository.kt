package com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.ServiceFirebaseApi
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.UserFirebaseApi
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao.ServiceDao
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao.UserDao
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.interfaceRepositories.IServiceRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.interfaceRepositories.IUserRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ServiceRepository(val serviceDao: ServiceDao,
                        val serviceFirebaseApi: ServiceFirebaseApi) : BaseRepository(), IServiceRepository {

    override fun insert(service: Service, userId: String) {
        launch {
            serviceDao.insert(service)
        }
        serviceFirebaseApi.insert(service, userId)
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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}