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

interface IBottomPanel : IPanel, BottomNavigationView.OnNavigationItemSelectedListener {

    var bottomPanel: BottomNavigationView

    fun initBottomPanel(selectedItemId: Int = -1) {
        bottomPanel = (panelContext as Activity).findViewById(R.id.bottomNavigationView)
        updateBottomPanel(selectedItemId)
        bottomPanel.setOnNavigationItemSelectedListener(this)
    }

    fun updateBottomPanel(selectedItemId: Int = -1) {
        bottomPanel.selectedItemId = selectedItemId
        bottomPanel.menu.findItem(selectedItemId)?.isEnabled = false
        if (selectedItemId == -1) {
            bottomPanel.menu.setGroupCheckable(0, false, true);
        }
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
        val intent = Intent(panelContext, ProfileActivity::class.java)
        intent.putExtra(User.USER, ProfileUserInteractor.cacheCurrentUser)
        panelContext.startActivity(intent)
        (panelContext as Activity).overridePendingTransition(0, 0)
    }

    fun goToMainScreen() {
        val intent = Intent(panelContext, MainScreenActivity::class.java)
        intent.putExtra(User.USER, ProfileUserInteractor.cacheCurrentUser)
        panelContext.startActivity(intent)
        (panelContext as Activity).overridePendingTransition(0, 0)
    }

    private fun goToDialogs() {
        val intent = Intent(panelContext, DialogsActivity::class.java)
        panelContext.startActivity(intent)
        (panelContext as Activity).overridePendingTransition(0, 0)
    }
}
