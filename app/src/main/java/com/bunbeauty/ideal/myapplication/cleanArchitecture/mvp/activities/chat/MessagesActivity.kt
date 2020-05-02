package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.chat

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.DialogAdapter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.MessageAdapter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat.MessagesDialogInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat.MessagesMessageInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.enums.ButtonTask
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.ITopPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.chat.MessagesPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.chat.MessagesView
import javax.inject.Inject

class MessagesActivity : MvpAppCompatActivity(), MessagesView, ITopPanel, View.OnClickListener {

    private lateinit var loadingMessagesProgressBar: ProgressBar
    private lateinit var resultsMessagesRecycleView: RecyclerView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageMessagesInput: EditText
    private lateinit var sendMessageMessagesBtn: Button

    @Inject
    lateinit var messageInteractor: MessagesMessageInteractor

    @Inject
    lateinit var messagesDialogInteractor: MessagesDialogInteractor

    @InjectPresenter
    lateinit var messagePresenter: MessagesPresenter

    @ProvidePresenter
    internal fun provideProfilePresenter(): MessagesPresenter {
        DaggerAppComponent.builder()
            .appModule(AppModule(application, intent))
            .build()
            .inject(this)
        return MessagesPresenter(messageInteractor, messagesDialogInteractor)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.messages)
        init()
        createPanels()
        messagePresenter.createMessageScreen()
    }

    private fun init() {
        loadingMessagesProgressBar = findViewById(R.id.loadingMessagesProgressBar)
        resultsMessagesRecycleView = findViewById(R.id.resultsMessagesRecycleView)
        messageMessagesInput = findViewById(R.id.messageMessagesInput)
        sendMessageMessagesBtn = findViewById(R.id.sendMessageMessagesBtn)
        sendMessageMessagesBtn.setOnClickListener(this)

        resultsMessagesRecycleView.layoutManager = LinearLayoutManager(this)
        messageAdapter = MessageAdapter(messagePresenter.getMessagesLink())
        resultsMessagesRecycleView.adapter = messageAdapter
    }

    private fun createPanels() {
        createTopPanel("Сообщение", ButtonTask.NONE, supportFragmentManager)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.sendMessageMessagesBtn -> {
                messagePresenter.sendMessage(messageMessagesInput.text.toString())
            }
        }
    }

    override fun showMessagesScreen(messages: List<Message>) {
        messageAdapter.notifyDataSetChanged()
    }

    override fun showSendMessage(message: Message) {
        messageAdapter.notifyDataSetChanged()
    }

    override fun hideLoading() {
        loadingMessagesProgressBar.visibility = View.GONE
    }

    override fun showLoading() {

    }

}