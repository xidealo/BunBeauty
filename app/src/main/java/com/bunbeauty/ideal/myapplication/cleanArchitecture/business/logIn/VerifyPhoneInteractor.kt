package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.iLogIn.IVerifyPhoneInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.BaseRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.UserRepository

class VerifyPhoneInteractor(private val userRepository: UserRepository) : BaseRepository(), IVerifyPhoneInteractor {
    private val TAG = "DBInf"

    override fun getMyPhoneNumber(): String = userRepository.getById("1").phone


}