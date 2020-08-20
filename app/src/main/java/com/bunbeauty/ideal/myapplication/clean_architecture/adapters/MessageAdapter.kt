package com.bunbeauty.ideal.myapplication.clean_architecture.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.clean_architecture.adapters.MessageAdapter.MessageViewHolder
import com.bunbeauty.ideal.myapplication.clean_architecture.adapters.elements.chatElements.messageElements.MessageServiceReviewElement
import com.bunbeauty.ideal.myapplication.clean_architecture.adapters.elements.chatElements.messageElements.MessageTextElement
import com.bunbeauty.ideal.myapplication.clean_architecture.adapters.elements.chatElements.messageElements.MessageUserReviewElement
import com.bunbeauty.ideal.myapplication.clean_architecture.business.DiffUtilCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.chat.MessagesPresenter

class MessageAdapter : RecyclerView.Adapter<MessageViewHolder>() {

    private var messageList = mutableListOf<Message>()
    private lateinit var messagesPresenter: MessagesPresenter

    fun setData(messagesPresenter: MessagesPresenter) {
        this.messagesPresenter = messagesPresenter
    }

    fun addItem(message: Message) {
        messageList.add(message)
        notifyItemInserted(messageList.size)
    }

    fun updateMessageAdapter(message: Message) {
        val foundMessage = messageList.find { it.id == message.id }
        if (foundMessage != null) {
            val index = messageList.indexOf(foundMessage)
            messageList[index] = message
            notifyItemChanged(index)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MessageViewHolder {
        val context = viewGroup.context
        val layoutIdForListItem = R.layout.element_message
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(layoutIdForListItem, viewGroup, false)
        return MessageViewHolder(view, context)
    }

    override fun onBindViewHolder(
        messageViewHolder: MessageViewHolder,
        index: Int
    ) {
        messageViewHolder.bind(messageList[index])
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    inner class MessageViewHolder(private val view: View, private val context: Context) :
        ViewHolder(view) {

        fun bind(message: Message) {

            when (message.type) {
                Message.TEXT_MESSAGE_STATUS -> {
                    val messageTextElement = MessageTextElement()
                    messageTextElement.createElement(view)
                    messageTextElement.setIsMyMessage(message)
                    messageTextElement.setVisibility(message)
                    messageTextElement.setData(message)
                }

                Message.SERVICE_REVIEW_MESSAGE_STATUS -> {
                    val messageTextElement =
                        MessageServiceReviewElement(messagesPresenter)
                    messageTextElement.createElement(view)
                    messageTextElement.setIsMyMessage(message)
                    messageTextElement.setVisibility(message)
                    messageTextElement.setData(message)
                }

                Message.USER_REVIEW_MESSAGE_STATUS -> {
                    val messageUserReviewElement =
                        MessageUserReviewElement(messagesPresenter)
                    messageUserReviewElement.createElement(view)
                    messageUserReviewElement.setIsMyMessage(message)
                    messageUserReviewElement.setVisibility(message)
                    messageUserReviewElement.setData(message)
                }
            }
        }
    }

}