package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces

import androidx.fragment.app.FragmentManager
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.fragments.general.BottomPanel

interface IBottomPanel {
    fun createBottomPanel(supportFragmentManager: FragmentManager, layoutId: Int) {
        val bottomPanel = BottomPanel()

        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(layoutId, bottomPanel)
        transaction.commit()
    }
}