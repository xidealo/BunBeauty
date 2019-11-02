package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService.iSearchService.IMainScreenInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.IUserSubscriber
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.ServiceRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.UserRepository
import com.google.firebase.auth.FirebaseAuth

class MainScreenInteractor(val userRepository: UserRepository,
                           val serviceRepository: ServiceRepository) : IMainScreenInteractor, IUserSubscriber {


    override fun getUserId(): String = FirebaseAuth.getInstance().currentUser!!.uid

    override fun createMainScreen() {
        //userRepository.getById(getUserId(),this)
    }


    override fun returnAddedUser(user: User) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }



}