package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.iProfile

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Subscription

interface IProfileSubscriptionInteractor {
    fun addSubscription(subscription: Subscription)
}