package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.adapters.MessageAdapter.MessageViewHolder
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.adapters.elements.chatElements.messageElements.MessageCancelElement
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.adapters.elements.chatElements.messageElements.MessageServiceReviewElement
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.adapters.elements.chatElements.messageElements.MessageTextElement
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.adapters.elements.chatElements.messageElements.MessageUserReviewElement
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.chat.MessagesPresenter

class MessageAdapter : RecyclerView.Adapter<MessageViewHolder>() {

    private var messageList = mutableListOf<Message>()
    private lateinit var messagesPresenter: MessagesPresenter

    fun setData(messagesPresenter: MessagesPresenter) {
        this.messagesPresenter = messagesPresenter
    }

    fun addItemToStart(message: Message) {
        messageList.add(message)
        messageList.sortBy { it.time }
        notifyItemInserted(0)
    }

    fun addItemToBottom(message: Message) {
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

    fun removeMessageAdapter(message: Message) {
        val foundMessage = messageList.find { it.id == message.id }
        if (foundMessage != null) {
            val index = messageList.indexOf(foundMessage)
            messageList.remove(foundMessage)
            notifyItemRemoved(index)
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

    override fun getItemCount() = messageList.size

    inner class MessageViewHolder(private val view: View, private val context: Context) :
        ViewHolder(view) {

        fun bind(message: Message) {

            when (message.type) {
                Message.TEXT_STATUS -> {
                    val messageTextElement = MessageTextElement(message, view)
                    messageTextElement.setIsMyMessage(message, view)
                    messageTextElement.setButtonVisibility(message, view, "")
                }

                Message.SERVICE_REVIEW_STATUS -> {
                    val messageTextElement =
                        MessageServiceReviewElement(messagesPresenter, message, view)
                    messageTextElement.setIsMyMessage(message, view)
                    messageTextElement.setButtonVisibility(
                        message,
                        view,
                        context.getString(R.string.rate)
                    )
                }

                Message.USER_REVIEW_STATUS -> {
                    val messageUserReviewElement =
                        MessageUserReviewElement(messagesPresenter, message, view)
                    messageUserReviewElement.setIsMyMessage(message, view)
                    messageUserReviewElement.setButtonVisibility(
                        message,
                        view,
                        context.getString(R.string.rate)
                    )
                }

                Message.CANCEL_STATUS -> {
                    val messageCancelElement =
                        MessageCancelElement(messagesPresenter, message, view)
                    messageCancelElement.setIsMyMessage(message, view)
                    messageCancelElement.setButtonVisibility(
                        message,
                        view,
                        context.getString(R.string.cancel)
                    )
                }

                else -> {
                    val messageTextElement = MessageTextElement(message, view)
                    messageTextElement.setIsMyMessage(message, view)
                    messageTextElement.setButtonVisibility(message, view, "")
                }
            }
        }
    }

}