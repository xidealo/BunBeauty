package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.serviceComments

import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.serviceComments.iServiceComments.IServiceCommentsServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service

class ServiceCommentsServiceInteractor(private val intent: Intent) :
    IServiceCommentsServiceInteractor {

    override fun getService() = intent.getSerializableExtra(Service.SERVICE) as Service

}