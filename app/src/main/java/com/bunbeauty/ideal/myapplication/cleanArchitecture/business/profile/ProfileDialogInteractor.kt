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

class ProfileDialogInteractor(private val dialogRepository: IDialogRepository) : DialogsCallback,
    IProfileDialogInteractor, InsertDialogCallback, DialogCallback, DialogChangedCallback {

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
        val myDialog = createMyDialog(user, ownerProfile)
        val companionDialog = createCompanionDialog(user, ownerProfile)
        dialogRepository.insert(
            listOf(
                companionDialog,
                myDialog
            ), this
        )
        profilePresenterCallback.goToDialog(myDialog, companionDialog)
    }

    override fun returnCreatedCallback(obj: Dialog) {
        //TODO (Переход сделать тут)
        if (obj.ownerId != User.getMyId()) {
            obj.user = ownerProfile
        }
    }

    private fun createMyDialog(user: User, companionUser: User): Dialog {
        val newDialog = Dialog()
        newDialog.id = user.id
        newDialog.user = companionUser
        newDialog.isChecked = true
        return newDialog
    }

    private fun createCompanionDialog(user: User, companionUser: User): Dialog {
        val newDialog = Dialog()
        newDialog.id = companionUser.id
        newDialog.user = user
        newDialog.isChecked = true
        return newDialog
    }

    override fun returnGottenObject(element: Dialog?) {
        if (element == null) return
    }

    override fun returnChanged(element: Dialog) {
        //Кто-то написал
    }
}