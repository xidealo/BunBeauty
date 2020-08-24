package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.chat

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.Tag
import com.bunbeauty.ideal.myapplication.clean_architecture.adapters.MessageAdapter
import com.bunbeauty.ideal.myapplication.clean_architecture.business.chat.i_chat.message.MessagesDialogInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.business.chat.i_chat.message.MessagesMessageInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.business.chat.i_chat.message.MessagesOrderInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.business.chat.i_chat.message.MessagesUserInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.business.chat.i_chat.message.i_message.IMessagesOrderInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.di.component.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.clean_architecture.di.module.AdapterModule
import com.bunbeauty.ideal.myapplication.clean_architecture.di.module.AppModule
import com.bunbeauty.ideal.myapplication.clean_architecture.di.module.FirebaseModule
import com.bunbeauty.ideal.myapplication.clean_architecture.di.module.InteractorModule
import com.bunbeauty.ideal.myapplication.clean_architecture.enums.ButtonTask
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.comments.CreationCommentActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.interfaces.ITopPanel
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.profile.ProfileActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.chat.MessagesPresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.chat.MessagesView
import kotlinx.android.synthetic.main.activity_messages.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent.setEventListener
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import javax.inject.Inject

class MessagesActivity : MvpAppCompatActivity(), MessagesView, ITopPanel {

    override var panelContext: Activity = this
    private var loadingLimit: Int = 15
    private var isScrolling = false
    private var isSmoothScrollingToPosition = true

    @Inject
    lateinit var messageInteractor: MessagesMessageInteractor

    @Inject
    lateinit var messagesDialogInteractor: MessagesDialogInteractor

    @Inject
    lateinit var messagesUserInteractor: MessagesUserInteractor

    @Inject
    lateinit var messagesOrderInteractor: MessagesOrderInteractor

    @Inject
    lateinit var messageAdapter: MessageAdapter

    @InjectPresenter
    lateinit var messagePresenter: MessagesPresenter

    @ProvidePresenter
    internal fun provideProfilePresenter(): MessagesPresenter {
        DaggerAppComponent.builder()
            .appModule(AppModule(application))
            .firebaseModule(FirebaseModule())
            .interactorModule(InteractorModule(intent))
            .adapterModule(AdapterModule())
            .build()
            .inject(this)

        return MessagesPresenter(
            messageInteractor,
            messagesDialogInteractor,
            messagesUserInteractor,
            messagesOrderInteractor
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)
        init()
        messagePresenter.getCompanionUser()
        messagePresenter.createMessageScreen(loadingLimit)
    }

    private fun init() {
        send_message_messages_btn.setOnClickListener {
            isSmoothScrollingToPosition = true
            messagePresenter.sendMessage(messageMessagesInput.text.toString().trim())
            messageMessagesInput.text.clear()
        }
        hideEmptyScreen()
        val linearLayoutManager = LinearLayoutManager(this)
        results_messages_recycle_view.layoutManager = linearLayoutManager
        results_messages_recycle_view.adapter = messageAdapter
        messageAdapter.setData(messagePresenter)

        results_messages_recycle_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (isScrolling && dy < 0 && linearLayoutManager.findFirstVisibleItemPosition() <= 3) {
                    Log.d(Tag.TEST_TAG, "Запрос в бд на докачку сообщений")
                    updateData()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true
                }
            }
        })
        setEventListener(
            this,
            object : KeyboardVisibilityEventListener {
                override fun onVisibilityChanged(isOpen: Boolean) {
                    moveToStart()
                }
            })
    }

    fun updateData() {
        isSmoothScrollingToPosition = false
        isScrolling = false
        loadingLimit += 25
        messagePresenter.createMessageScreen(loadingLimit)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data == null || resultCode != RESULT_OK) {
            return
        }

        when (requestCode) {
            REQUEST_MESSAGE_USER_REVIEW -> {
                messagePresenter.updateMessage(data.getSerializableExtra(Message.MESSAGE) as Message)
                messagePresenter.updateUser(data.getSerializableExtra(User.USER) as User)
            }
        }
    }

    override fun showMessage(message: Message) {
        messageAdapter.addItem(message, isSmoothScrollingToPosition)
        if (isSmoothScrollingToPosition)
            moveToStart()
    }

    override fun updateMessageAdapter(message: Message) {
        messageAdapter.updateMessageAdapter(message)
    }

    override fun moveToStart() {
        results_messages_recycle_view.smoothScrollToPosition(messageAdapter.itemCount)
    }

    override fun hideLoading() {
        loading_messages_progress_bar.visibility = View.GONE
    }

    override fun showLoading() {
        loading_messages_progress_bar.visibility = View.VISIBLE
    }

    override fun showCompanionUser(fullName: String, photoLink: String) {
        initTopPanel(
            fullName, ButtonTask.GO_TO_PROFILE, photoLink
        )
    }

    override fun hideEmptyScreen() {
        empty_messages_text.visibility = View.GONE
    }

    override fun showEmptyScreen() {
        empty_messages_text.visibility = View.VISIBLE
    }

    override fun goToProfile(user: User) {
        val intent = Intent(this, ProfileActivity::class.java)
        intent.putExtra(User.USER, user)
        startActivity(intent)
    }

    override fun actionClick() {
        messagePresenter.goToProfile()
    }

    override fun goToCreationComment(user: User, message: Message, dialog: Dialog) {
        val intent = Intent(this, CreationCommentActivity::class.java).apply {
            putExtra(User.USER, user)
            putExtra(Message.MESSAGE, message)
            putExtra(Dialog.DIALOG, dialog)
        }

        startActivityForResult(
            intent,
            REQUEST_MESSAGE_USER_REVIEW
        )
        overridePendingTransition(0, 0)
    }

    companion object {
        const val REQUEST_MESSAGE_USER_REVIEW = 1
    }
}