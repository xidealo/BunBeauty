package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.chat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.Tag
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.adapters.MessageAdapter
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.chat.i_chat.message.MessagesDialogInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.chat.i_chat.message.MessagesMessageInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.chat.i_chat.message.MessagesOrderInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.chat.i_chat.message.MessagesUserInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.chat.i_chat.message.i_message.MessagesScheduleInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.api.gone
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.api.visible
import com.bunbeauty.ideal.myapplication.clean_architecture.enums.ButtonTask
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.comments.CreationCommentActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.profile.ProfileActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.base.BaseActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.chat.MessagesPresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.chat.MessagesView
import kotlinx.android.synthetic.main.activity_messages.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent.setEventListener
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import javax.inject.Inject

class MessagesActivity : BaseActivity(), MessagesView {

    private var loadingLimit: Int = 25
    private var isScrolling = false

    @Inject
    lateinit var messageInteractor: MessagesMessageInteractor

    @Inject
    lateinit var messagesDialogInteractor: MessagesDialogInteractor

    @Inject
    lateinit var messagesUserInteractor: MessagesUserInteractor

    @Inject
    lateinit var messagesOrderInteractor: MessagesOrderInteractor

    @Inject
    lateinit var messagesScheduleInteractor: MessagesScheduleInteractor

    @Inject
    lateinit var messageAdapter: MessageAdapter

    @InjectPresenter
    lateinit var messagePresenter: MessagesPresenter

    @ProvidePresenter
    internal fun provideProfilePresenter(): MessagesPresenter {
        buildDagger().inject(this)

        return MessagesPresenter(
            messageInteractor,
            messagesDialogInteractor,
            messagesUserInteractor,
            messagesOrderInteractor,
            messagesScheduleInteractor,
            intent
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)
        init()
        messagePresenter.getCompanionUser()
        messagePresenter.createMessageScreen(loadingLimit)
    }

    override fun onStop() {
        super.onStop()
        messagePresenter.removeObservers()
    }

    private fun init() {
        activity_messages_btn_send.setOnClickListener {
            messagePresenter.sendMessage(activity_messages_et_message.text.toString().trim())
            activity_messages_et_message.setText("")
        }
        hideEmptyScreen()
        val linearLayoutManager = LinearLayoutManager(this)
        activity_messages_rv_messages.layoutManager = linearLayoutManager
        activity_messages_rv_messages.adapter = messageAdapter
        messageAdapter.setData(messagePresenter)

        activity_messages_rv_messages.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (isScrolling && dy < 0)
                    hideKeyboard()

                if (isScrolling && dy < 30 && linearLayoutManager.findFirstVisibleItemPosition() <= 3) {
                    Log.d(Tag.TEST_TAG, "Запрос на докачку сообщений")
                    messagePresenter.setIsSmoothScrollingToPosition(false)
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
                    if (isOpen)
                        moveToStart()
                }
            })
    }

    fun updateData() {
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

    override fun addItemToBottom(message: Message) {
        messageAdapter.addItemToBottom(message)
    }

    override fun addItemToStart(message: Message) {
        messageAdapter.addItemToStart(message)
    }

    override fun updateMessageAdapter(message: Message) {
        messageAdapter.updateMessageAdapter(message)
    }

    override fun removeMessageAdapter(message: Message) {
        messageAdapter.removeMessageAdapter(message)
    }

    override fun moveToStart() {
        activity_messages_rv_messages.smoothScrollToPosition(messageAdapter.itemCount)
    }

    override fun hideLoading() {
        activity_messages_pb_loading.gone()
    }

    override fun showLoading() {
        activity_messages_pb_loading.visible()
    }

    override fun showCompanionUser(fullName: String, photoLink: String) {
        initTopPanel(
            fullName, ButtonTask.GO_TO_PROFILE, photoLink
        )
    }

    override fun hideEmptyScreen() {
        activity_messages_tv_empty.gone()
    }

    override fun showEmptyScreen() {
        activity_messages_tv_empty.visible()
    }

    override fun goToProfile(user: User) {
        val intent = Intent(this, ProfileActivity::class.java)
        intent.putExtra(User.USER, user)
        startActivity(intent)
        overridePendingTransition(0, 0)
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

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(
            R.anim.anim_slide_in_right,
            R.anim.anim_slide_out_right
        )
    }

    companion object {
        const val REQUEST_MESSAGE_USER_REVIEW = 1
    }
}