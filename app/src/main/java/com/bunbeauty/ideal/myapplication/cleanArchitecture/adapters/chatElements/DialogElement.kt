package com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.chatElements

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.helpApi.CircularTransformation
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithStringsApi
import com.squareup.picasso.Picasso

class DialogElement(
    private val view: View
) : View.OnClickListener {

    private lateinit var avatarDialogElementImage: ImageView
    private lateinit var nameDialogElementText: TextView
    private lateinit var lastMessageDialogElementText: TextView
    private lateinit var messageTimeDialogElementText: TextView

    fun createElement(dialog: Dialog) {
        onViewCreated(view)
        setData(dialog)
    }

    private fun onViewCreated(view: View) {
        avatarDialogElementImage = view.findViewById(R.id.avatarDialogElementImage)
        nameDialogElementText = view.findViewById(R.id.nameDialogElementText)
        lastMessageDialogElementText = view.findViewById(R.id.lastMessageDialogElementText)
        messageTimeDialogElementText = view.findViewById(R.id.messageTimeDialogElementText)
    }

    private fun setData(dialog: Dialog) {
        showAvatar(dialog.user)
        nameDialogElementText.text = "${dialog.user.name} ${dialog.user.surname}"
        lastMessageDialogElementText.text = dialog.lastMessage.message
        messageTimeDialogElementText.text = dialog.lastMessage.time
    }

    private fun showAvatar(user: User) {
        Picasso.get()
            .load(user.photoLink)
            .resize(65, 65)
            .centerCrop()
            .transform(CircularTransformation())
            .into(avatarDialogElementImage)
    }

    override fun onClick(v: View) {
        goToDialog()
    }

    private fun goToDialog() {}

    companion object {
        private const val TAG = "DBInf"
    }

}