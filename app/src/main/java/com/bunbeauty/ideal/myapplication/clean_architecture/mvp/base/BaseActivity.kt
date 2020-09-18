package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.base

import android.app.Activity
import android.app.Application
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.App
import com.bunbeauty.ideal.myapplication.clean_architecture.di.component.ActivityComponent
import com.bunbeauty.ideal.myapplication.clean_architecture.di.component.AppComponent
import com.bunbeauty.ideal.myapplication.clean_architecture.di.component.DaggerActivityComponent
import com.bunbeauty.ideal.myapplication.clean_architecture.di.component.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.clean_architecture.di.module.AppModule
import com.bunbeauty.ideal.myapplication.clean_architecture.di.module.FirebaseModule
import com.bunbeauty.ideal.myapplication.clean_architecture.di.module.InteractorModule
import com.bunbeauty.ideal.myapplication.clean_architecture.di.module.RepositoryModule
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.interfaces.IBottomPanel
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.interfaces.ITopPanel
import com.google.android.material.snackbar.Snackbar

abstract class BaseActivity : MvpAppCompatActivity(), ITopPanel, IBottomPanel {
    /**
     * for [ITopPanel] and [IBottomPanel]
     */
    override var panelContext: Activity = this

    /**
     * for [DaggerActivityComponent]
     */
    fun buildDagger(): AppComponent {
        return DaggerAppComponent
            .builder()
            .application(application)
            .build()
    }

    /**
     * animation
     */
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, 0)
    }

    /**
     * show [Snackbar] with message
     */
    fun showMessage(message: String, layout: View) {
        val snack = Snackbar.make(layout, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(this, R.color.mainBlue40))
            .setActionTextColor(ContextCompat.getColor(this, R.color.white))
        snack.view.findViewById<TextView>(R.id.snackbar_text).textAlignment =
            View.TEXT_ALIGNMENT_CENTER
        snack.show()
    }

    fun hideKeyboard() {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = currentFocus
        if (view == null) {
            view = View(this)
        }
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        view.clearFocus()
    }

}