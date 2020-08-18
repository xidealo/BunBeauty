package com.bunbeauty.ideal.myapplication.clean_architecture.business.profile.iProfile

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.profile.ProfilePresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Subscription

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