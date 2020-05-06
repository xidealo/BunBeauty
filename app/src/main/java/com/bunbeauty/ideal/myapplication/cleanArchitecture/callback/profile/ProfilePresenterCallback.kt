package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.profile

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

interface ProfilePresenterCallback {
    fun setUserProfile(user: User)
    fun showSubscribed()
    fun showUnsubscribed()
    fun setServiceList(serviceList: List<Service>)
    fun showMyProfile(user: User)
    fun showAlienProfile(user: User)
    fun showRating(rating: Float)
    fun showWithoutRating()
    fun goToEditProfile(user: User)
    fun goToDialog(dialog: Dialog)

    fun getProfileServiceList(userId: String)
}