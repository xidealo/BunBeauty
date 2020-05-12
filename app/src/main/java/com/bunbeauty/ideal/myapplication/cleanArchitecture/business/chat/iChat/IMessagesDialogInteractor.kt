package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat.iChat

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Message

interface IMessagesDialogInteractor {
    fun getMyDialog(): Dialog
    fun getCompanionDialog(): Dialog
    fun updateUncheckedDialog(message: Message)
    fun updateCheckedDialog()
    fun setUnchecked()
}