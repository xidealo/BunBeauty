package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.adapters.DayAdapter
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.sessions.SessionsInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.sessions.SessionsMessageInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.sessions.SessionsOrderInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.schedule.Session
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.schedule.WorkingDay
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.api.gone
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.api.invisible
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.api.visible
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.base.BaseActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.SessionsPresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.SessionsView
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.activity_sessions.*
import javax.inject.Inject

class SessionsActivity : BaseActivity(), SessionsView {

    private val timeButtonList: MutableList<MaterialButton> = ArrayList()

    @Inject
    lateinit var sessionsInteractor: SessionsInteractor

    @Inject
    lateinit var sessionsOrderInteractor: SessionsOrderInteractor

    @Inject
    lateinit var sessionsMessageInteractor: SessionsMessageInteractor

    @InjectPresenter
    lateinit var sessionsPresenter: SessionsPresenter

    @ProvidePresenter
    internal fun provideSessionsPresenter(): SessionsPresenter {
        buildDagger().inject(this)
        return SessionsPresenter(
            sessionsInteractor,
            sessionsOrderInteractor,
            sessionsMessageInteractor,
            intent
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sessions)

        init()
        sessionsPresenter.getSchedule()

        initTopPanel("Сеансы")
    }

    override fun onResume() {
        super.onResume()

        initBottomPanel()
    }

    private fun init() {
        activity_session_btn_make_appointment.invisible()
        activity_session_btn_make_appointment.setOnClickListener {
            sessionsPresenter.makeAppointment()
        }
    }

    override fun showDays(days: List<WorkingDay>) {
        activity_session_sv.visible()
        activity_session_btn_make_appointment.visible()

        activity_session_rv_days.layoutManager = LinearLayoutManager(this, HORIZONTAL, false)
        activity_session_rv_days.adapter = DayAdapter(days, sessionsPresenter)
    }

    override fun showNoAvailableSessions() {
        activity_session_tv_no_available_sessions.visibility = View.VISIBLE
    }

    override fun showTime(sessions: List<Session>) {
        timeButtonList.clear()

        val width = resources.displayMetrics.widthPixels / TIME_COUNT_ON_SCREEN
        val height = resources.getDimensionPixelSize(R.dimen.session_time_button_height)

        activity_session_gl_sessions.columnCount = TIME_COUNT_ON_SCREEN

        for (session in sessions) {
            val text = session.getStringStartTime()
            val button = createButton(width, height, text)

            timeButtonList.add(button)
            addViewToContainer(button, activity_session_gl_sessions)
        }
    }

    @SuppressLint("InflateParams")
    private fun createButton(width: Int, height: Int, text: String): MaterialButton {
        val button = LayoutInflater.from(this).inflate(R.layout.element_schedule_button, null) as MaterialButton

        val margin = resources.getDimensionPixelSize(R.dimen.session_time_button_margin)
        button.layoutParams = LinearLayout.LayoutParams(width - 2 * margin, height).apply {
            setMargins(margin, 0, margin, 0)
        }
        button.text = text
        button.setOnClickListener {
            sessionsPresenter.updateTime(text)
        }

        return button
    }

    private fun addViewToContainer(view: View, container: ViewGroup) {
        if (view.parent != null) {
            (view.parent as ViewGroup).removeView(view)
        }
        container.addView(view)
    }

    override fun clearTime(time: String) {
        val unselectedTimeButton = timeButtonList.find { it.text == time }
        unselectedTimeButton?.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
    }

    override fun selectTime(selectedTime: String) {
        val selectedButton = timeButtonList.find { it.text == selectedTime }
        selectedButton?.setBackgroundColor(ContextCompat.getColor(this, R.color.yellow))
    }

    override fun showMessage(message: String) {
        showMessage(message, activity_session_cl_main)
    }

    override fun goBack() {
        onBackPressed()
    }

    override fun clearSessionsLayout() {
        activity_session_gl_sessions.removeAllViews()
    }

    override fun enableMakeAppointmentButton() {
        activity_session_btn_make_appointment.isEnabled = true
    }

    override fun disableMakeAppointmentButton() {
        activity_session_btn_make_appointment.isEnabled = false
    }

    override fun showLoading() {
        activity_session_pb.visible()
        activity_session_sv.gone()
        activity_session_btn_make_appointment.gone()
        activity_session_tv_no_available_sessions.gone()
    }

    override fun hideLoading() {
        activity_session_pb.gone()
    }

    companion object {
        const val TIME_COUNT_ON_SCREEN = 6
    }
}