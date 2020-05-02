package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat.iChat

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.chat.DialogsPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

interface IDialogsDialogInteractor {
    fun getDialogs(dialogsPresenterCallback: DialogsPresenterCallback)
    fun getDialogsLink():List<Dialog>
    fun fillDialogs(users: List<User>, dialogsPresenterCallback: DialogsPresenterCallback)
}