package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.fragments.general

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.chat.DialogsActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.ProfileUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
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
            R.id.chatBottomPanelText -> goTo(DialogsActivity::class.java)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_panel, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        profileText = view.findViewById(R.id.profileBottomPanelText)
        mainScreenText = view.findViewById(R.id.mainScreenBottomPanelText)
        chatText = view.findViewById(R.id.chatBottomPanelText)

        if ((context is ProfileActivity) /*&&
                (context as ProfileActivity).profilePresenter.isUserOwner()*/) {
            profileText.setTextColor(ContextCompat.getColor(context!!, R.color.yellow))
        } else {
            profileText.setOnClickListener(this)
        }

        if (activity is MainScreenActivity) {
            mainScreenText.setTextColor(ContextCompat.getColor(context!!, R.color.yellow))
        } else {
            mainScreenText.setOnClickListener(this)
        }

        if (activity is DialogsActivity) {
            chatText.setTextColor(ContextCompat.getColor(context!!, R.color.yellow))
        } else {
            chatText.setOnClickListener(this)
        }
    }

    private fun goTo(activityClass: Class<out Activity>) {
        val intent = Intent(context, activityClass)
        this.startActivity(intent)
        (context as Activity).overridePendingTransition(0,0)
    }

    private fun goToProfile() {
        val intent = Intent(context, ProfileActivity::class.java)
        intent.putExtra(User.USER, ProfileUserInteractor.cacheCurrentUser)
        this.startActivity(intent)
        (context as Activity).overridePendingTransition(0,0)
    }

    fun goToMainScreen(){
        val intent = Intent(context, MainScreenActivity::class.java)
        intent.putExtra(User.USER, ProfileUserInteractor.cacheCurrentUser)
        this.startActivity(intent)
        (context as Activity).overridePendingTransition(0,0)
    }
}