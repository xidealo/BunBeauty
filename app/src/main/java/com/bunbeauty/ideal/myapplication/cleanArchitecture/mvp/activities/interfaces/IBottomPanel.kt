package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.MenuItem
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.ProfileUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.chat.DialogsActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.profile.ProfileActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.searchService.MainScreenActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

interface IBottomPanel : BottomNavigationView.OnNavigationItemSelectedListener {

    var bottomNavigationContext: Context

    fun initBottomPanel(selectedItemId: Int = -1) {
        val bottomPanel: BottomNavigationView =
            (bottomNavigationContext as Activity).findViewById(R.id.bottomNavigationView)
        bottomPanel.selectedItemId = selectedItemId
        bottomPanel.menu.findItem(selectedItemId)?.isEnabled = false
        bottomPanel.setOnNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_profile -> {

                goToProfile()
            }
            R.id.navigation_main -> {

                goToMainScreen()
            }
            R.id.navigation_chat -> {
                goToDialogs()
            }
        }

        return true
    }

    private fun goToProfile() {
        val intent = Intent(bottomNavigationContext, ProfileActivity::class.java)
        intent.putExtra(User.USER, ProfileUserInteractor.cacheCurrentUser)
        bottomNavigationContext.startActivity(intent)
        (bottomNavigationContext as Activity).overridePendingTransition(0, 0)
    }

    fun goToMainScreen() {
        val intent = Intent(bottomNavigationContext, MainScreenActivity::class.java)
        intent.putExtra(User.USER, ProfileUserInteractor.cacheCurrentUser)
        bottomNavigationContext.startActivity(intent)
        (bottomNavigationContext as Activity).overridePendingTransition(0, 0)
    }

    private fun goToDialogs() {
        val intent = Intent(bottomNavigationContext, DialogsActivity::class.java)
        bottomNavigationContext.startActivity(intent)
        (bottomNavigationContext as Activity).overridePendingTransition(0, 0)
    }
}
