package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service

import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.iProfile.IServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.IServiceSubscriber
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.IUserServiceCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.IUserSubscriber
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.BaseRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.PhotoRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.ServiceRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.UserRepository
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithTimeApi

class ServiceInteractor(private val userRepository: UserRepository,
                        private val serviceRepository: ServiceRepository,
                        private val intent: Intent) : BaseRepository(),
        IServiceInteractor, IUserSubscriber, IServiceSubscriber {

    private lateinit var userServiceCallback: IUserServiceCallback
    private lateinit var service: Service

    fun getService(userServiceCallback: IUserServiceCallback) {
        this.userServiceCallback = userServiceCallback

        serviceRepository.getById(intent.getStringExtra(Service.SERVICE_ID),
                intent.getStringExtra(Service.USER_ID),
                this)
    }

    override fun returnService(service: Service) {
        this.service = service

        //photoRepository.getByServiceId(service.id)
        userRepository.getById(service.userId, this, false)
    }

    override fun returnUser(user: User) {
        userServiceCallback.returnUserAndService(user, service)
    }

    fun isPremium(premiumDate: String): Boolean = WorkWithTimeApi.checkPremium(premiumDate)

    override fun returnUsers(users: List<User>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun returnServiceList(serviceList: List<Service>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}