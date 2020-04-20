package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces

import androidx.fragment.app.FragmentManager
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.cleanArchitecture.enums.ButtonTask
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.fragments.general.TopPanel

interface ITopPanel {

    fun createTopPanel(title: String, buttonTask: ButtonTask, fragmentManager: FragmentManager) {
        val topPanel =
            TopPanel.newInstance(
                title,
                buttonTask
            )

        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.topPanelLayout, topPanel)
        transaction.commit()
    }

    fun createTopPanel(
        title: String,
        buttonTask: ButtonTask,
        photoLink: String,
        fragmentManager: FragmentManager
    ) {
        val topPanel =
            TopPanel.newInstance(
                title,
                buttonTask,
                photoLink
            )

        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.topPanelLayout, topPanel)
        transaction.commit()
    }

    fun iconClick() {}
}