package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat.iChat

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Dialog

interface IMessagesDialogInteractor {
    fun getMyDialog(): Dialog
    fun getCompanionDialog(): Dialog
    fun updateDialog(dialog: Dialog)
    fun updateCompanionDialog(dialog: Dialog)
}