package com.bunbeauty.ideal.myapplication.clean_architecture.domain.service

import android.content.Intent
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.service.i_service.IServiceUserInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.service.ServicePresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.user.UserCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.UserRepository

class ServiceUserInteractor(
    private val userRepository: UserRepository
) : IServiceUserInteractor, UserCallback {

    private lateinit var servicePresenterCallback: ServicePresenterCallback
    private lateinit var gottenUser: User

    override fun checkMaster(
        intent: Intent,
        userId: String,
        servicePresenterCallback: ServicePresenterCallback
    ) {
        this.servicePresenterCallback = servicePresenterCallback

        if (intent.hasExtra(User.USER)) {
            returnGottenObject(intent.getSerializableExtra(User.USER) as User)
        } else {
            userRepository.getById(userId, this, true)
        }
    }

    override fun returnGottenObject(user: User?) {
        if (user == null) return
        gottenUser = user

        if (isMyService(user)) {
            servicePresenterCallback.createOwnServiceTopPanel()
            servicePresenterCallback.showMyService()
        } else {
            servicePresenterCallback.createAlienServiceTopPanel(user)
            servicePresenterCallback.showAlienService()
        }
    }

    private fun isMyService(user: User): Boolean {
        return User.getMyId() == user.id
    }

    override fun getUser(): User {
        return gottenUser
    }
}