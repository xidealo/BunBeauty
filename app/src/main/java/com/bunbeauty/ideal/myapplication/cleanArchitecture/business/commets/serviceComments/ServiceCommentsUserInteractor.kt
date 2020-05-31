package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.serviceComments

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.serviceComments.iServiceComments.IServiceCommentsUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.IUserRepository

class ServiceCommentsUserInteractor(private val userRepository: IUserRepository) :
    IServiceCommentsUserInteractor {

}