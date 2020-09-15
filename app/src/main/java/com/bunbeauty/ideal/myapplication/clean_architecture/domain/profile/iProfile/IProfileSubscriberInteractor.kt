package com.bunbeauty.ideal.myapplication.clean_architecture.domain.profile.iProfile

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.profile.ProfilePresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Subscriber
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User

interface IProfileSubscriberInteractor {
    fun checkSubscriber(
        subscriber: Subscriber,
        profilePresenterCallback: ProfilePresenterCallback
    )

    fun checkSubscribed(
        userId: String,
        user: User,
        profilePresenterCallback: ProfilePresenterCallback
    )
}