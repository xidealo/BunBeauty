package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat.iChat

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.chat.DialogsPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Dialog

interface IDialogsMessageInteractor {
    fun getMyMessages(
        dialogs: List<Dialog>,
        companionDialogs: List<Dialog>,
        dialogsPresenterCallback: DialogsPresenterCallback
    )
}