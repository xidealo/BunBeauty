package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service

import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service.iService.IServiceUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.service.ServicePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.user.UserCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.UserRepository

class ServiceUserInteractor(
    private val userRepository: UserRepository,
    private val intent: Intent
) : IServiceUserInteractor, UserCallback {

    private lateinit var servicePresenterCallback: ServicePresenterCallback
    private lateinit var gottenUser: User

    override fun getUser(userId: String, servicePresenterCallback: ServicePresenterCallback) {
        this.servicePresenterCallback = servicePresenterCallback

        if (intent.hasExtra(User.USER)) {
            returnGottenObject(intent.getSerializableExtra(User.USER) as User)
        } else {
            userRepository.getById(userId, this, true)
        }
    }

    override fun returnGottenObject(user: User?) {
        gottenUser = user!!

        if (isMyService(user)) {
            servicePresenterCallback.createOwnServiceTopPanel()
        } else {
            servicePresenterCallback.showPremium()
            servicePresenterCallback.createAlienServiceTopPanel(user)
        }
    }

    private fun isMyService(user: User): Boolean {
        return User.getMyId() == user.id
    }

    override fun getUser(): User {
        return gottenUser
    }
}