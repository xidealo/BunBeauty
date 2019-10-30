package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService.iSearchService.IMainScreenInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.ServiceRepository
import com.google.firebase.auth.FirebaseAuth

class MainScreenInteractor(val serviceRepository: ServiceRepository) : IMainScreenInteractor {

    override fun getUserId(): String = FirebaseAuth.getInstance().currentUser!!.uid

}