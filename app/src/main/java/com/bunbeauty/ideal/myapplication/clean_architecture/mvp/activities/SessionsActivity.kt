package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities

import android.graphics.Point
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.api.invisible
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.api.visible
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.base.BaseActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.SessionsPresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.SessionsView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_sessions.*
import javax.inject.Inject

class SessionsActivity : BaseActivity(), SessionsView{

    private val timeButtonList: MutableList<Button> = ArrayList()

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
            sessionsMessageInteractor
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

        val width = resources.getDimensionPixelSize(R.dimen.sessions_button_width)
        val height = resources.getDimensionPixelSize(R.dimen.sessions_button_height)

        activity_session_gl_sessions.columnCount = getColumnCount(width)

        for (session in sessions) {
            val text = session.getStringStartTime()
            val button = getConfiguredButton(width, height, text)

            timeButtonList.add(button)
            addViewToContainer(button, activity_session_gl_sessions)
        }
    }

    private fun getColumnCount(buttonWidth: Int): Int {
        val size = Point()
        windowManager.defaultDisplay.getRealSize(size)
        val margin = resources.getDimensionPixelSize(R.dimen.sessions_button_margin)
        return size.x / (buttonWidth + 2 * margin)
    }

    private fun getConfiguredButton(width: Int, height: Int, text: String): Button {
        val button = Button(this)

        val params = LinearLayout.LayoutParams(width, height)
        val margin = resources.getDimensionPixelSize(R.dimen.sessions_button_margin)
        params.setMargins(margin, margin, margin, margin)
        button.layoutParams = params
        setBackground(button)
        button.text = text
        button.setOnClickListener {
            sessionsPresenter.updateTime(text)
        }

        return button
    }

    private fun setBackground(button: Button) {
        val gradientDrawable = GradientDrawable()
        gradientDrawable.cornerRadius = resources.getDimension(R.dimen.schedule_button_corner_radius)
        gradientDrawable.setColor(ContextCompat.getColor(this, R.color.white))

        button.background = gradientDrawable
    }

    private fun addViewToContainer(view: View, container: ViewGroup) {
        if (view.parent != null) {
            (view.parent as ViewGroup).removeView(view)
        }
        container.addView(view)
    }

    override fun clearTime(time: String) {
        val unselectedTimeButton = timeButtonList.find { it.text == time }
        if (unselectedTimeButton != null) {
            setBackground(unselectedTimeButton)
        }
    }

    override fun selectTime(selectedTime: String) {
        fillButton(timeButtonList.find { it.text == selectedTime }!!)
    }

    private fun fillButton(button: Button) {
        val gradientDrawable = button.background as GradientDrawable

        gradientDrawable.setColor(ContextCompat.getColor(this, R.color.yellow))
        button.background = gradientDrawable
    }

    override fun showMessage(message: String) {
        Snackbar.make(activity_session_cl_main, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(this, R.color.mainBlue))
            .setActionTextColor(ContextCompat.getColor(this, R.color.white)).show()
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
        activity_session_pb.visibility = View.VISIBLE
        activity_session_sv.visibility = View.GONE
        activity_session_btn_make_appointment.visibility = View.GONE
        activity_session_tv_no_available_sessions.visibility = View.GONE
    }

    override fun hideLoading() {
        activity_session_pb.visibility = View.GONE
    }
}