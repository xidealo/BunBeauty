package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service

import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.iProfile.IServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.IServiceSubscriber
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.IUserSubscriber
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.ProfileCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.ServiceCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.BaseRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.ServiceRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId

class ServiceInteractor(private val userRepository: UserRepository,
                        private val serviceRepository: ServiceRepository,
                        private val intent: Intent) : BaseRepository(),
        IServiceInteractor, IUserSubscriber, IServiceSubscriber {

    private val SERVICE_ID = "service id"
    private val USER_ID = "service id"

    lateinit var serviceCallback: ServiceCallback

    fun getService(serviceCallback: ServiceCallback) {
        this.serviceCallback = serviceCallback

        //getUser()

        serviceRepository.getById(intent.getStringExtra(Service.SERVICE_ID),
                intent.getStringExtra(Service.USER_ID),
                this)
    }

    /*private fun getUser() {
        userRepository.getById(intent)
    }*/

    override fun returnService(service: Service) {
       serviceCallback.callbackGetService(service)
    }

    override fun returnUser(user: User) {
    }

    override fun returnServiceList(serviceList: List<Service>) {
    }

}