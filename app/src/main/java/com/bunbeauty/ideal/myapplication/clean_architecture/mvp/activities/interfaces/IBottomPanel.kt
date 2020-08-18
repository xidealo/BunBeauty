package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.interfaces

import android.content.Intent
import android.view.MenuItem
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.clean_architecture.business.profile.ProfileUserInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.chat.DialogsActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.profile.ProfileActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.search_service.MainScreenActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.part_bottom_panel.*

interface IBottomPanel : IPanel, BottomNavigationView.OnNavigationItemSelectedListener {

    fun initBottomPanel(selectedItemId: Int = -1) {
        updateBottomPanel(selectedItemId)
        panelContext.bottomPanel.setOnNavigationItemSelectedListener(this)
    }

    fun updateBottomPanel(selectedItemId: Int = -1) {
        panelContext.bottomPanel.selectedItemId = selectedItemId
        panelContext.bottomPanel.menu.findItem(selectedItemId)?.isEnabled = false
        if (selectedItemId == -1) {
            panelContext.bottomPanel.menu.setGroupCheckable(0, false, true);
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
        intent.putExtra(User.USER, ProfileUserInteractor.cacheUser)
        startActivity(intent)
    }

    fun goToMainScreen() {
        val intent = Intent(panelContext, MainScreenActivity::class.java)
        intent.putExtra(User.USER, ProfileUserInteractor.cacheUser)
        startActivity(intent)
    }

    private fun goToDialogs() {
        val intent = Intent(panelContext, DialogsActivity::class.java)
        startActivity(intent)
    }

    private fun startActivity(intent: Intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        panelContext.startActivity(intent)
        panelContext.overridePendingTransition(0, 0)
        panelContext.finish()
    }
}
