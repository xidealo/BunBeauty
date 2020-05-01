package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.iProfile.IProfileDialogInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.profile.ProfilePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.dialog.DialogsCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.dialog.InsertDialogCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.DialogRepository

class ProfileDialogInteractor(private val dialogRepository: DialogRepository) :
    IProfileDialogInteractor, InsertDialogCallback, DialogsCallback {

    private lateinit var profilePresenterCallback: ProfilePresenterCallback
    private lateinit var ownerProfile: User
    override fun goToDialog(
        userId: String,
        ownerProfile: User,
        profilePresenterCallback: ProfilePresenterCallback
    ) {
        this.profilePresenterCallback = profilePresenterCallback
        this.ownerProfile = ownerProfile
        dialogRepository.getByUserId(userId, this)
    }

    override fun returnList(objects: List<Dialog>) {

        val dialog = objects.find { it.user.id == ownerProfile.id }

        if (dialog == null) {
            val dialog = Dialog()
            dialog.user = ownerProfile
            dialog.isChecked = true

            dialogRepository.insert(dialog, this)
            return
        }

        dialog.user = ownerProfile
        profilePresenterCallback.goToDialog(dialog)
    }

    override fun returnCreatedCallback(obj: Dialog) {

    }

}