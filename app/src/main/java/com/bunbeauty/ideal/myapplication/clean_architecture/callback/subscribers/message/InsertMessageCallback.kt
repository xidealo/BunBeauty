package com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.message

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.base_subscribers.BaseInsertCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Message

interface InsertMessageCallback:BaseInsertCallback<Message>