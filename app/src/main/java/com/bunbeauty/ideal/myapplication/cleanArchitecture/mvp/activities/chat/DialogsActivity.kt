package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.chat

import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat.DialogsDialogInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat.DialogsUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.enums.ButtonTask
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.IBottomPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.ITopPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.chat.DialogsPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.chat.DialogsView
import java.util.*
import javax.inject.Inject

class DialogsActivity : MvpAppCompatActivity(), IBottomPanel, ITopPanel, DialogsView {

    private lateinit var progressBar: ProgressBar
    private lateinit var manager: FragmentManager
    private lateinit var dialogList: ArrayList<Dialog>
    private lateinit var recyclerView: RecyclerView
    private lateinit var noDialogsText: TextView

    @Inject
    lateinit var dialogsDialogInteractor: DialogsDialogInteractor

    @Inject
    lateinit var dialogsUserInteractor: DialogsUserInteractor

    @InjectPresenter
    lateinit var dialogsPresenter: DialogsPresenter

    @ProvidePresenter
    internal fun provideProfilePresenter(): DialogsPresenter {
        DaggerAppComponent.builder()
            .appModule(AppModule(application, intent))
            .build()
            .inject(this)
        return DialogsPresenter(dialogsDialogInteractor, dialogsUserInteractor)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialogs)
        createPanels()
        init()
        dialogsPresenter.getDialogs()
    }

    private fun init() {
        recyclerView = findViewById(R.id.resultsDialogsRecycleView)
        progressBar = findViewById(R.id.progressBarDialogs)
        noDialogsText = findViewById(R.id.noDialogsDialogsText)
        dialogList = ArrayList()
        val layoutManager = LinearLayoutManager(this)
        /*recyclerView.setLayoutManager(layoutManager)*/
        manager = supportFragmentManager
    }

    private fun createPanels() {
        createBottomPanel(supportFragmentManager)
        createTopPanel("Диалоги", ButtonTask.NONE, supportFragmentManager)
    }

}