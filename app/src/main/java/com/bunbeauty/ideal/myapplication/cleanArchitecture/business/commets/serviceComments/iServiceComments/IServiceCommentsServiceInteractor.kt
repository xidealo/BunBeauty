package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.serviceComments.iServiceComments

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service

interface IServiceCommentsServiceInteractor {
    fun getService(): Service
}