package com.bunbeauty.ideal.myapplication.clean_architecture.business.service

import android.content.Intent
import com.bunbeauty.ideal.myapplication.clean_architecture.business.service.i_service.IServiceUserInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.service.ServicePresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.user.UserCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.UserRepository

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