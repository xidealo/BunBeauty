package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.base

import android.app.Application
import com.arellomobile.mvp.MvpAppCompatFragment
import com.bunbeauty.ideal.myapplication.clean_architecture.di.component.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.clean_architecture.di.component.DaggerFragmentComponent
import com.bunbeauty.ideal.myapplication.clean_architecture.di.component.FragmentComponent

abstract class BaseFragment : MvpAppCompatFragment() {

    fun buildDagger(): FragmentComponent {
        val parent = DaggerAppComponent
            .builder()
            .application(activity!!.application)
            .build()

        return DaggerFragmentComponent.builder().appComponent(parent).build()
    }

}