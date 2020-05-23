package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.iProfile

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.profile.ProfilePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

interface IProfileDialogInteractor {

    fun goToDialog(
        user: User,
        profileOwner: User,
        profilePresenterCallback: ProfilePresenterCallback
    )

}