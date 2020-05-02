package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.chat

import android.os.Bundle
import android.widget.EditText
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat.MessagesMessageInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.ITopPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.chat.MessagesPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.chat.MessagesView
import javax.inject.Inject

class MessagesActivity : MvpAppCompatActivity(), MessagesView, ITopPanel {

    private lateinit var loadingMessagesProgressBar: ProgressBar
    private lateinit var resultsMessagesRecycleView: RecyclerView
    private lateinit var messageMessagesInput: EditText

    @Inject
    lateinit var messageInteractor: MessagesMessageInteractor

    @InjectPresenter
    lateinit var messagePresenter: MessagesPresenter

    @ProvidePresenter
    internal fun provideProfilePresenter(): MessagesPresenter {
        DaggerAppComponent.builder()
            .appModule(AppModule(application, intent))
            .build()
            .inject(this)
        return MessagesPresenter(messageInteractor)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.messages)
    }

    private fun init() {
        loadingMessagesProgressBar = findViewById(R.id.loadingMessagesProgressBar)
        resultsMessagesRecycleView = findViewById(R.id.resultsMessagesRecycleView)
        messageMessagesInput = findViewById(R.id.messageMessagesInput)
    }

    private fun createPanels() {

    }

}