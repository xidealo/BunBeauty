package com.bunbeauty.ideal.myapplication.clean_architecture.domain.profile.iProfile

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.profile.ProfilePresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User

interface IProfileDialogInteractor {

    fun getDialog(
        user: User,
        profileOwner: User,
        profilePresenterCallback: ProfilePresenterCallback
    )

}