package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.iProfile

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.profile.ProfilePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Subscription

interface IProfileSubscriptionInteractor {
    fun getSubscriptions(
        myUserId: String, profilePresenterCallback: ProfilePresenterCallback
    )

    fun addSubscription(
        subscription: Subscription,
        profilePresenterCallback: ProfilePresenterCallback
    )

    fun deleteSubscription(
        subscription: Subscription,
        profilePresenterCallback: ProfilePresenterCallback
    )
}