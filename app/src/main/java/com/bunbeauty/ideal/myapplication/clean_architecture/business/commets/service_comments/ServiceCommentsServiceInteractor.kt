package com.bunbeauty.ideal.myapplication.clean_architecture.business.commets.service_comments

import android.content.Intent
import com.bunbeauty.ideal.myapplication.clean_architecture.business.commets.service_comments.iServiceComments.IServiceCommentsServiceInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service

class ServiceCommentsServiceInteractor(private val intent: Intent) :
    IServiceCommentsServiceInteractor {

    override fun getService() = intent.getSerializableExtra(Service.SERVICE) as Service

}