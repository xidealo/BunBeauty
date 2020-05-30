package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.iProfile.IProfileDialogInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.profile.ProfilePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.dialog.DialogCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.dialog.DialogChangedCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.dialog.DialogsCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.dialog.InsertDialogCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.IDialogRepository

class ProfileDialogInteractor(private val dialogRepository: IDialogRepository) :
    IProfileDialogInteractor, InsertDialogCallback, DialogsCallback, DialogCallback,
    DialogChangedCallback {

    private lateinit var profilePresenterCallback: ProfilePresenterCallback
    private lateinit var ownerProfile: User
    private lateinit var user: User

    override fun goToDialog(
        user: User,
        profileOwner: User,
        profilePresenterCallback: ProfilePresenterCallback
    ) {
        this.profilePresenterCallback = profilePresenterCallback
        this.ownerProfile = profileOwner
        this.user = user

        dialogRepository.getByUserId(user.id, this, this, this)
    }

    override fun returnList(objects: List<Dialog>) {

        val dialog = objects.find { it.user.id == ownerProfile.id }

        if (dialog == null) {
            dialogRepository.insert(listOf(createCompanionDialog(), createMyDialog()), this)
            return
        }

        dialog.user = ownerProfile
        profilePresenterCallback.goToDialog(dialog)
    }

    override fun returnCreatedCallback(obj: Dialog) {
        if (obj.ownerId != User.getMyId()) {
            obj.user = ownerProfile
        }

        profilePresenterCallback.goToDialog(obj)
    }

    private fun createMyDialog(): Dialog {
        val newDialog = Dialog()

        newDialog.user = user
        newDialog.ownerId = ownerProfile.id
        newDialog.isChecked = true
        return newDialog
    }

    private fun createCompanionDialog(): Dialog {
        val newDialog = Dialog()

        newDialog.user = ownerProfile
        newDialog.ownerId = user.id
        newDialog.isChecked = true
        return newDialog
    }

    override fun returnElement(element: Dialog) {

    }

    override fun returnChanged(element: Dialog) {
        //Кто-то написал
    }
}