package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities

import android.annotation.SuppressLint
import android.content.Intent
import com.arellomobile.mvp.MvpAppCompatActivity
import com.bunbeauty.ideal.myapplication.chat.Dialogs
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.profile.ProfileActivity
import com.bunbeauty.ideal.myapplication.searchService.MainScreen

@SuppressLint("Registered")
open class BaseActivity : MvpAppCompatActivity() {
    fun goToProfile() {
        val intent = Intent(this, ProfileActivity::class.java)
        this.startActivity(intent)
    }

    fun goToMainScreen() {
        val intent = Intent(this, MainScreen::class.java)
        this.startActivity(intent)
    }

    fun goToChat() {
        val intent = Intent(this, Dialogs::class.java)
        this.startActivity(intent)
    }
}