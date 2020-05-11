package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat.iChat

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.chat.DialogsPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Dialog

interface IDialogsUserInteractor {
    fun getUser(dialog: Dialog, dialogsPresenterCallback: DialogsPresenterCallback)
    fun clearCache()
}