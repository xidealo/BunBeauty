package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.chat

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.MessageAdapter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat.MessagesDialogInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat.MessagesMessageInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat.MessagesUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.FirebaseModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.InteractorModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.enums.ButtonTask
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.ITopPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.profile.ProfileActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.chat.MessagesPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.chat.MessagesView
import kotlinx.android.synthetic.main.activity_messages.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent.setEventListener
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import javax.inject.Inject

class MessagesActivity : MvpAppCompatActivity(), MessagesView, ITopPanel, View.OnClickListener {

    private lateinit var messageAdapter: MessageAdapter

    override var panelContext: Activity = this

    @Inject
    lateinit var messageInteractor: MessagesMessageInteractor

    @Inject
    lateinit var messagesDialogInteractor: MessagesDialogInteractor

    @Inject
    lateinit var messagesUserInteractor: MessagesUserInteractor

    @InjectPresenter
    lateinit var messagePresenter: MessagesPresenter

    @ProvidePresenter
    internal fun provideProfilePresenter(): MessagesPresenter {
        DaggerAppComponent.builder()
            .appModule(AppModule(application))
            .firebaseModule(FirebaseModule())
            .interactorModule(InteractorModule(intent))
            .build()
            .inject(this)
        return MessagesPresenter(
            messageInteractor,
            messagesDialogInteractor,
            messagesUserInteractor
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)
        init()
        messagePresenter.getCompanionUser()
        messagePresenter.createMessageScreen()
    }

    private fun init() {
        sendMessageMessagesBtn.setOnClickListener(this)

        val linearLayoutManager = LinearLayoutManager(this)
        resultsMessagesRecycleView.layoutManager = linearLayoutManager
        messageAdapter = MessageAdapter(messagePresenter.getMessagesLink(), messagePresenter)
        resultsMessagesRecycleView.adapter = messageAdapter

        setEventListener(
            this,
            object : KeyboardVisibilityEventListener {
                override fun onVisibilityChanged(isOpen: Boolean) {
                    messagePresenter.checkMoveToStart()
                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data == null || resultCode != RESULT_OK) {
            return
        }

        when (requestCode) {
            REQUEST_MESSAGE_USER_REVIEW -> {
                messagePresenter.updateMessage(data.getSerializableExtra(Message.MESSAGE) as Message)
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.sendMessageMessagesBtn -> {
                messagePresenter.sendMessage(messageMessagesInput.text.toString().trim())
                messageMessagesInput.text.clear()
            }
        }
    }

    override fun showMessagesScreen(messages: List<Message>) {
        messageAdapter.notifyDataSetChanged()
    }

    override fun moveToStart() {
        resultsMessagesRecycleView.smoothScrollToPosition(resultsMessagesRecycleView.adapter!!.itemCount - 1)
    }

    override fun showSendMessage(message: Message) {
        messageAdapter.notifyDataSetChanged()
    }

    override fun hideLoading() {
        loadingMessagesProgressBar.visibility = View.GONE
    }

    override fun showLoading() {
        loadingMessagesProgressBar.visibility = View.VISIBLE
    }

    override fun showCompanionUser(fullName: String, photoLink: String) {
        initTopPanel(
            fullName, ButtonTask.GO_TO_PROFILE, photoLink
        )
    }

    override fun goToProfile(user: User) {
        val intent = Intent(this, ProfileActivity::class.java)
        intent.putExtra(User.USER, user)
        startActivity(intent)
    }

    override fun actionClick() {
        messagePresenter.goToProfile()
    }

    companion object {
        const val REQUEST_MESSAGE_USER_REVIEW = 1

    }
}