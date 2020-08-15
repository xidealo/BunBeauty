package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.chat

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.DialogAdapter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat.DialogsDialogInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat.DialogsMessageInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat.DialogsUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.enums.ButtonTask
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.IBottomPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.ITopPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.chat.DialogsPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.chat.DialogsView
import kotlinx.android.synthetic.main.activity_dialogs.*
import javax.inject.Inject

class DialogsActivity : MvpAppCompatActivity(), IBottomPanel, ITopPanel, DialogsView {

    override var panelContext: Activity = this

    @Inject
    lateinit var dialogsDialogInteractor: DialogsDialogInteractor

    @Inject
    lateinit var dialogsUserInteractor: DialogsUserInteractor

    @Inject
    lateinit var dialogsMessageInteractor: DialogsMessageInteractor

    @Inject
    lateinit var dialogAdapter: DialogAdapter

    @InjectPresenter
    lateinit var dialogsPresenter: DialogsPresenter

    @ProvidePresenter
    internal fun provideProfilePresenter(): DialogsPresenter {
        DaggerAppComponent.builder()
            .appModule(AppModule(application))
            .firebaseModule(FirebaseModule())
            .interactorModule(InteractorModule(intent))
            .adapterModule(AdapterModule())
            .build()
            .inject(this)
        return DialogsPresenter(
            dialogsDialogInteractor,
            dialogsUserInteractor,
            dialogsMessageInteractor
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialogs)
        createPanels()
        init()
        hideEmptyDialogs()
        dialogsPresenter.getDialogs()
    }

    override fun onResume() {
        super.onResume()
        initBottomPanel(R.id.navigation_chat)
    }

    private fun init() {
        resultsDialogsRecycleView.layoutManager = LinearLayoutManager(this)
        resultsDialogsRecycleView.adapter = dialogAdapter
    }

    private fun createPanels() {
        initTopPanel("Диалоги", ButtonTask.NONE)
    }

    override fun showDialogs(dialogList: List<Dialog>) {
        dialogAdapter.setData(dialogList)
    }

    override fun showLoading() {
        progressBarDialogs.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progressBarDialogs.visibility = View.GONE
    }

    override fun showEmptyDialogs() {
        noDialogsDialogsText.visibility = View.VISIBLE
    }

    override fun hideEmptyDialogs() {
        noDialogsDialogsText.visibility = View.GONE
    }

}