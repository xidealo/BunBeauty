package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.base

import android.app.Activity
import com.arellomobile.mvp.MvpAppCompatActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.di.component.AppComponent
import com.bunbeauty.ideal.myapplication.clean_architecture.di.component.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.clean_architecture.di.module.AppModule
import com.bunbeauty.ideal.myapplication.clean_architecture.di.module.FirebaseModule
import com.bunbeauty.ideal.myapplication.clean_architecture.di.module.InteractorModule
import com.bunbeauty.ideal.myapplication.clean_architecture.di.module.RepositoryModule
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.interfaces.IBottomPanel
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.interfaces.ITopPanel

abstract class BaseActivity : MvpAppCompatActivity(), ITopPanel, IBottomPanel {
    /**
     * for [ITopPanel] and [IBottomPanel]
    */
    override var panelContext: Activity = this

    /**
     * for [DaggerAppComponent]
     */
    fun buildDagger(): AppComponent {
        return DaggerAppComponent
            .builder()
            .appModule(AppModule(application))
            .firebaseModule(FirebaseModule())
            .interactorModule(InteractorModule(intent))
            .repositoryModule(RepositoryModule())
            .build()
    }

    /**
     * animation
     */
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, 0)
    }

}