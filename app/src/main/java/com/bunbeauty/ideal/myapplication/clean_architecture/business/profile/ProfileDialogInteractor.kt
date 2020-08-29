package com.bunbeauty.ideal.myapplication.clean_architecture.business.profile

import com.bunbeauty.ideal.myapplication.clean_architecture.business.profile.iProfile.IProfileDialogInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.profile.ProfilePresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.dialog.DialogCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.dialog.DialogChangedCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.dialog.DialogsCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories.IDialogRepository

class ProfileDialogInteractor(private val dialogRepository: IDialogRepository) : DialogsCallback,
    IProfileDialogInteractor, DialogCallback, DialogChangedCallback {

    private lateinit var profilePresenterCallback: ProfilePresenterCallback
    private lateinit var profileOwner: User
    private lateinit var user: User

    override fun getDialog(
        user: User,
        profileOwner: User,
        profilePresenterCallback: ProfilePresenterCallback
    ) {
        this.profilePresenterCallback = profilePresenterCallback
        this.profileOwner = profileOwner
        this.user = user

        dialogRepository.getByUserId(user.id, this, this, this)
    }

    override fun returnList(objects: List<Dialog>) {
        val myDialog = createMyDialog(user, profileOwner)
        val companionDialog = createCompanionDialog(user, profileOwner)
        profilePresenterCallback.goToMessages(myDialog, companionDialog)
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