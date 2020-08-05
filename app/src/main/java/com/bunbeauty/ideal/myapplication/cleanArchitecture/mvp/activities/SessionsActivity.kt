package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities

import android.app.Activity
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.sessions.SessionsInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.ScheduleWithDays
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.WorkingDay
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.FirebaseModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.InteractorModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.enums.ButtonTask
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.IBottomPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.ITopPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.SessionsPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.SessionsView
import kotlinx.android.synthetic.main.activity_sessions.*
import javax.inject.Inject

class SessionsActivity : MvpAppCompatActivity(), SessionsView, ITopPanel, IBottomPanel {

    override var panelContext: Activity = this

    private val daysButtons = ArrayList<Button>()
    private val sessionsButtons = ArrayList<Button>()

    @Inject
    lateinit var sessionsInteractor: SessionsInteractor

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

        return SessionsPresenter(sessionsInteractor)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sessions)

        sessionsPresenter.getSchedule()

        initTopPanel("Сеансы", ButtonTask.NONE)
    }

    override fun onResume() {
        super.onResume()

        initBottomPanel()
    }

    override fun createDaysButtons(days: List<WorkingDay>) {
        for (day in days) {
            val width= resources.getDimensionPixelSize(R.dimen.schedule_button_width)
            val height= resources.getDimensionPixelSize(R.dimen.schedule_button_height)
            val text = day.getDayOfMonth().toString()
            val button = getConfiguredButton(width, height, text)
            daysButtons.add(button)

            addViewToContainer(button, daysSessionsLayout)
        }
    }

    private fun getConfiguredButton(width: Int, height: Int, text: String): Button {
        val button = Button(this)

        val margin = resources.getDimensionPixelSize(R.dimen.schedule_button_margin)
        val params = LinearLayout.LayoutParams(width - 2 * margin, height)
        params.setMargins(margin, margin, margin, margin)
        button.layoutParams = params
        setBackground(button)
        button.setTag(R.id.touchedTag, NOT_TOUCHED)
        button.text = text
        button.setOnClickListener {
            val buttonIndex = daysButtons.indexOf(button)
            if (sessionsPresenter.isDaySelected(buttonIndex)) {
                clearButtonFill(button)
                sessionsPresenter.clearSelectedDay()
                hideSessionsLayout()
            } else {
                clearButtonFill(daysButtons[sessionsPresenter.getSelectedDay()])
                sessionsPresenter.setSelectedDay(buttonIndex)
                fillButton(button)

                val sessions = sessionsPresenter.getSessions(button.text.toString())
                // TODO show sessions
            }
        }

        return button
    }

    private fun setBackground(button: Button) {
        val gradientDrawable = GradientDrawable()
        gradientDrawable.cornerRadius = resources.getDimension(R.dimen.button_corner_radius)
        gradientDrawable.setStroke(0, ContextCompat.getColor(this, R.color.white))
        gradientDrawable.setColor(ContextCompat.getColor(this, R.color.white))

        button.background = gradientDrawable
    }

    private fun getScreenWidth(): Int {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

    private fun getScreenHeight(): Int {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    private fun addViewToContainer(view: View, container: ViewGroup) {
        if (view.parent != null) {
            (view.parent as ViewGroup).removeView(view)
        }
        container.addView(view)
    }

    /*private fun selectButton(button: Button) {
        val gradientDrawable = button.background as GradientDrawable

        gradientDrawable.cornerRadius = resources.getDimension(R.dimen.button_corner_radius)
        gradientDrawable.setStroke(
            resources.getDimensionPixelSize(R.dimen.button_stroke_width),
            ContextCompat.getColor(this, R.color.mainBlue)
        )
        button.background = gradientDrawable

        button.setTag(R.id.touchedTag, TOUCHED)
    }

    private fun clearButtonSelection(button: Button) {
        val gradientDrawable = button.background as GradientDrawable

        gradientDrawable.setStroke(0, ContextCompat.getColor(this, R.color.white))
        button.background = gradientDrawable

        button.setTag(R.id.touchedTag, NOT_TOUCHED)
    }*/

    private fun clearButtonFill(button: Button) {
        val gradientDrawable = button.background as GradientDrawable

        gradientDrawable.setColor(ContextCompat.getColor(this, R.color.white))
        button.background = gradientDrawable

        button.setTag(R.id.touchedTag, NOT_TOUCHED)
    }

    private fun fillButton(button: Button) {
        val gradientDrawable = button.background as GradientDrawable

        gradientDrawable.setColor(ContextCompat.getColor(this, R.color.yellow))
        button.background = gradientDrawable

        button.setTag(R.id.touchedTag, TOUCHED)
    }

    private fun hideSessionsLayout() {
        timeSessionGrid.visibility = View.GONE
    }

    private fun showSessionsLayout() {
        timeSessionGrid.visibility = View.VISIBLE
    }

    companion object {
        private const val TIME_COLUMN_COUNT = 6

        private const val TOUCHED = "touched"
        private const val NOT_TOUCHED = "not touched"
    }
}