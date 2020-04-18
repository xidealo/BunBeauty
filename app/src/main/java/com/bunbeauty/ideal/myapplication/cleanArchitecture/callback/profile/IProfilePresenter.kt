package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.profile

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

interface IProfilePresenter {
    fun setUserProfile(user: User)
    fun setServiceListWithOwner(serviceList: List<Service>, user: User)
    fun showMyProfile()
    fun showAlienProfile()
    fun showRating(rating:Float)
    fun showWithoutRating()
}