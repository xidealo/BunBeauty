package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.chat

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.adapters.DialogAdapter
import com.bunbeauty.ideal.myapplication.clean_architecture.business.chat.i_chat.dialog.DialogsDialogInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.business.chat.i_chat.dialog.DialogsMessageInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.business.chat.i_chat.dialog.DialogsUserInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.clean_architecture.enums.ButtonTask
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.base.BaseActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.chat.DialogsPresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.chat.DialogsView
import kotlinx.android.synthetic.main.activity_dialogs.*
import javax.inject.Inject

class DialogsActivity : BaseActivity(), DialogsView {

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
        buildDagger().inject(this)
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

    override fun onStop() {
        super.onStop()
        dialogsPresenter.removeObservers()
    }

    private fun init() {
        activity_dialogs_rv_dialogs.layoutManager = LinearLayoutManager(this)
        activity_dialogs_rv_dialogs.adapter = dialogAdapter
    }

    private fun createPanels() {
        initTopPanel("Диалоги", ButtonTask.NONE)
    }

    override fun showDialogs(dialog: Dialog) {
        dialogAdapter.addItem(dialog)
    }

    override fun showLoading() {
        activity_dialogs_pb_loading.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        activity_dialogs_pb_loading.visibility = View.GONE
    }

    override fun showEmptyDialogs() {
        activity_dialogs_tv_empty.visibility = View.VISIBLE
    }

    override fun hideEmptyDialogs() {
        activity_dialogs_tv_empty.visibility = View.GONE
    }

}