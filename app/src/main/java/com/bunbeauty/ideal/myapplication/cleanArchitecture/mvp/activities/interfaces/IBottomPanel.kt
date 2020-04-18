package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces

import androidx.fragment.app.FragmentManager
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.fragments.general.BottomPanel

interface IBottomPanel {
    @Deprecated("use default layout")
    fun createBottomPanel(supportFragmentManager: FragmentManager, layoutId: Int) {
        val bottomPanel = BottomPanel()

        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(layoutId, bottomPanel)
        transaction.commit()
    }

    fun createBottomPanel(fragmentManager: FragmentManager) {
        val bottomPanel = BottomPanel()

        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.bottomPanelLayout, bottomPanel)
        transaction.commit()
    }
}