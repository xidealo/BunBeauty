package com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.IServiceSubscriber
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.ServiceFirebaseApi
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao.ServiceDao
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.interfaceRepositories.IServiceRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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

    override fun getServicesByCityActual(userCity: String) {

        //возвращение всех пользователей из контретного города
        val userQuery = FirebaseDatabase.getInstance().getReference(User.USERS)
                .orderByChild(User.CITY)
                .equalTo(userCity)

        userQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(usersSnapshot: DataSnapshot) {
                //callback
               /* val commonList = search.getServicesOfUsers(usersSnapshot,
                        null, null, null,
                        category,
                        selectedTagsArray)
                for (serviceData in commonList) {
                    serviceList.add(serviceData[1] as Service)
                    userList.add(serviceData[2] as User)
                }
                serviceAdapter = ServiceAdapter(serviceList.size, serviceList, userList)
                recyclerView.adapter = serviceAdapter
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE*/
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    override fun getServicesByCityAndCategory(userCity: String, category: String, selectedTagsArray: java.util.ArrayList<String>?) {
        //callback
        /* val commonList = search.getServicesOfUsers(usersSnapshot,
                 null, null, null,
                 category,
                 selectedTagsArray)
         for (serviceData in commonList) {
             serviceList.add(serviceData[1] as Service)
             userList.add(serviceData[2] as User)
         }
         serviceAdapter = ServiceAdapter(serviceList.size, serviceList, userList)
         recyclerView.adapter = serviceAdapter
         progressBar.visibility = View.GONE
         recyclerView.visibility = View.VISIBLE*/
    }
    fun getIdForNew(userId: String): String = serviceFirebaseApi.getIdForNew(userId)

}