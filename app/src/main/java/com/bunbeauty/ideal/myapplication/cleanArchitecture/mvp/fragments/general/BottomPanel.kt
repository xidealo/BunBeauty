package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.fragments.general

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.chat.Dialogs
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.profile.ProfileActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.searchService.MainScreenActivity

class BottomPanel : Panel() {

    private lateinit var profileText: TextView
    private lateinit var mainScreenText: TextView
    private lateinit var chatText: TextView

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.profileBottomPanelText -> goToProfile()
            R.id.mainScreenBottomPanelText -> goToMainScreen()
            R.id.chatBottomPanelText -> goToChat()
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

        if (activity is MainScreenActivity) {
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

    private fun goToProfile() {
        val intent = Intent(context, ProfileActivity::class.java)
        this.startActivity(intent)
    }

    private fun goToMainScreen() {
        val intent = Intent(context, MainScreenActivity::class.java)
        this.startActivity(intent)
    }

    private fun goToChat() {
        val intent = Intent(context, Dialogs::class.java)
        this.startActivity(intent)
    }
}