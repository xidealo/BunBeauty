package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities

import android.app.Activity
import android.graphics.Point
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.DayAdapter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.sessions.SessionsInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.sessions.SessionsOrderInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.Session
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.WorkingDay
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.FirebaseModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.InteractorModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.IBottomPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.ITopPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.SessionsPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.SessionsView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_creation_service.*
import kotlinx.android.synthetic.main.activity_sessions.*
import javax.inject.Inject

class SessionsActivity : MvpAppCompatActivity(), SessionsView, ITopPanel, IBottomPanel {

    override var panelContext: Activity = this

    private val timeButtonList: MutableList<Button> = ArrayList()

    @Inject
    lateinit var sessionsInteractor: SessionsInteractor

    @Inject
    lateinit var sessionsOrderInteractor: SessionsOrderInteractor

    @InjectPresenter
    lateinit var sessionsPresenter: SessionsPresenter

    @ProvidePresenter
    internal fun provideSessionsPresenter(): SessionsPresenter {
        DaggerAppComponent.builder()
            .appModule(AppModule(application))
            .firebaseModule(FirebaseModule())
            .interactorModule(InteractorModule(intent))
            .build()
            .inject(this)

        return SessionsPresenter(sessionsInteractor, sessionsOrderInteractor)
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
        makeAppointmentSessionBtn.setOnClickListener {
            sessionsPresenter.makeAppointment()
        }
    }

    override fun showDays(days: List<WorkingDay>) {
        daysSessionsRecyclerView.layoutManager = LinearLayoutManager(this, HORIZONTAL, false)
        daysSessionsRecyclerView.adapter = DayAdapter(days, sessionsPresenter)
    }

    override fun showTime(sessions: List<Session>) {
        timeButtonList.clear()

        val width = resources.getDimensionPixelSize(R.dimen.sessions_button_width)
        val height = resources.getDimensionPixelSize(R.dimen.sessions_button_height)

        timeSessionGrid.columnCount = getColumnCount(width)

        for (session in sessions) {
            val text = session.getStringStartTime()
            val button = getConfiguredButton(width, height, text)

            timeButtonList.add(button)
            addViewToContainer(button, timeSessionGrid)
        }
    }

    private fun getColumnCount(buttonWidth: Int): Int {
        val size = Point()
        windowManager.defaultDisplay.getRealSize(size)
        return size.x / (buttonWidth + 2 * BUTTON_MARGIN)
    }

    private fun getConfiguredButton(width: Int, height: Int, text: String): Button {
        val button = Button(this)

        val params = LinearLayout.LayoutParams(width, height)
        params.setMargins(BUTTON_MARGIN, BUTTON_MARGIN, BUTTON_MARGIN, BUTTON_MARGIN)
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
        gradientDrawable.cornerRadius = resources.getDimension(R.dimen.button_corner_radius)
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
        Snackbar.make(sessionsMainLayout, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(this, R.color.mainBlue))
            .setActionTextColor(ContextCompat.getColor(this, R.color.white)).show()
    }

    override fun goBack() {
        onBackPressed()
    }

    override fun clearSessionsLayout() {
        timeSessionGrid.removeAllViews()
    }

    override fun enableMakeAppointmentButton() {
        makeAppointmentSessionBtn.isEnabled = true
    }

    override fun disableMakeAppointmentButton() {
        makeAppointmentSessionBtn.isEnabled = false
    }

    companion object {
        const val BUTTON_MARGIN = 8
    }
}