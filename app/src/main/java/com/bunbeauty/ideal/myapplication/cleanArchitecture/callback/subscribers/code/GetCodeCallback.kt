package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.code

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Code

interface GetCodeCallback {
    fun returnCode(code: Code)
}