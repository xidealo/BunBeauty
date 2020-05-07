package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.profile

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

interface ProfilePresenterCallback {
    fun returnProfileOwner(user: User)
    fun setServiceList(serviceList: List<Service>)
    fun showMyProfile(user: User)
    fun showAlienProfile(user: User)
    fun goToEditProfile(user: User)
    fun goToDialog(dialog: Dialog)
    fun showSubscribed()
    fun showUnsubscribed()

    fun getProfileServiceList(userId: String)

    fun showUpdatedBottomPanel(selectedItemId: Int = -1)
}
