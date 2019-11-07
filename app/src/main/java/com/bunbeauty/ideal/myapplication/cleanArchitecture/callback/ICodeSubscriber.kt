package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Code

interface ICodeSubscriber {
    fun returnCode(code: Code)
}