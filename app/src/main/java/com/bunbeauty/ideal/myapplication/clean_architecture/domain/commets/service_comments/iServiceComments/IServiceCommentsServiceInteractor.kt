package com.bunbeauty.ideal.myapplication.clean_architecture.domain.commets.service_comments.iServiceComments

import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service

interface IServiceCommentsServiceInteractor {
    fun getService(): Service
}