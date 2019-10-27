package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.fragments.general

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.chat.Dialogs
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.BaseActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.profile.ProfileActivity
import com.bunbeauty.ideal.myapplication.searchService.MainScreen

class BottomPanel : Panel() {

    private lateinit var profileText: TextView
    private lateinit var mainScreenText: TextView
    private lateinit var chatText: TextView

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.profileBottomPanelText -> (activity as BaseActivity).goToProfile()
            R.id.mainScreenBottomPanelText -> (activity as BaseActivity).goToMainScreen()
            R.id.chatBottomPanelText -> (activity as BaseActivity).goToChat()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_panel, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        profileText = view.findViewById(R.id.profileBottomPanelText)
        mainScreenText = view.findViewById(R.id.mainScreenBottomPanelText)
        chatText = view.findViewById(R.id.chatBottomPanelText)

        if ((context is ProfileActivity) &&
                (context as ProfileActivity).profilePresenter.isUserOwner()) {
            profileText.setTextColor(ContextCompat.getColor(context!!, R.color.yellow))
        } else {
            profileText.setOnClickListener(this)
        }

        if (activity is MainScreen) {
            mainScreenText.setTextColor(ContextCompat.getColor(context!!, R.color.yellow))
        } else {
            mainScreenText.setOnClickListener(this)
        }

        if (activity is Dialogs) {
            chatText.setTextColor(ContextCompat.getColor(context!!, R.color.yellow))
        } else {
            chatText.setOnClickListener(this)
        }
    }
}