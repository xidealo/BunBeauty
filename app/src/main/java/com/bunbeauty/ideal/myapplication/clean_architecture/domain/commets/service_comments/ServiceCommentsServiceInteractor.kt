package com.bunbeauty.ideal.myapplication.clean_architecture.domain.commets.service_comments

import android.content.Intent
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.commets.service_comments.iServiceComments.IServiceCommentsServiceInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service

class ServiceCommentsServiceInteractor : IServiceCommentsServiceInteractor {

    override fun getService(intent: Intent) = intent.getParcelableExtra<Service>(Service.SERVICE) as Service

}