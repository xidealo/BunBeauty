package com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.elements.chatElements

import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithTimeApi
import java.util.*


class MessageElement(
    private val view: View
) {
    private lateinit var message: Message
    private lateinit var messageTextMessageElementText: TextView
    private lateinit var messageTimeMessageElementText: TextView
    private lateinit var mainLayoutMessageElementLayout: LinearLayout

    fun createElement(message: Message) {
        this.message = message
        onViewCreated(view)
        setData(message)
    }

    private fun onViewCreated(view: View) {
        messageTextMessageElementText = view.findViewById(R.id.messageTextMessageElementText)
        messageTimeMessageElementText = view.findViewById(R.id.messageTimeMessageElementText)
        mainLayoutMessageElementLayout = view.findViewById(R.id.mainLayoutMessageElementLayout)
    }

    private fun setData(message: Message) {
        messageTextMessageElementText.text = message.message
        messageTimeMessageElementText.text =
            WorkWithTimeApi.getDateInFormatYMDHMS(Date(message.time)).substring(11, 16)

        if (message.userId == User.getMyId()) {
            mainLayoutMessageElementLayout.gravity = Gravity.END
        } else {
            mainLayoutMessageElementLayout.gravity = Gravity.START
        }
    }
}
