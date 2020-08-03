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

        init()
        createDaysButtons()
        initTopPanel("Сеансы", ButtonTask.NONE)
    }

    override fun onResume() {
        super.onResume()

        initBottomPanel()
        sessionsPresenter.getSchedule()
    }

    private fun init() {
        sessionsLayout.visibility = View.GONE
    }

    private fun createDaysButtons() {
        for (weekIndex in 0 until WEEK_COUNT) {
            for (weekDayIndex in 0 until WEEK_DAY_COUNT) {
                val width = getScreenWidth() / WEEK_DAY_COUNT
                val height = resources.getDimensionPixelSize(R.dimen.schedule_button_height)
                val text =
                    sessionsPresenter.getDateString(weekIndex * WEEK_DAY_COUNT + weekDayIndex)
                val button = getConfiguredButton(width, height, text)
                setButtonEnabled(button, weekIndex * WEEK_DAY_COUNT + weekDayIndex)

                daysButtons.add(button)
            }
        }

        for (i in 0 until daysButtons.size) {
            addViewToContainer(daysButtons[i], daysSessionsGrid)
        }
    }

    private fun setButtonEnabled(button: Button, dayIndex: Int) {
        button.isEnabled = sessionsPresenter.isPastDay(dayIndex)
    }

    fun setSchedule(schedule: ScheduleWithDays) {
        for (workingDay in schedule.workingDays) {
            val dayIndex = sessionsPresenter.getDayIndex(workingDay.workingDay.date)
            if (dayIndex > 0 && dayIndex < daysButtons.size) {
                fillButton(daysButtons[dayIndex])
            }
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
                clearButtonSelection(button)
                sessionsPresenter.clearSelectedDay(buttonIndex)
            } else {
                clearButtonSelection(daysButtons[sessionsPresenter.getSelectedDay()])
                sessionsPresenter.setSelectedDay(buttonIndex)
                selectButton(button)
            }
        }

        return button
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

    private fun setBackground(button: Button) {
        val gradientDrawable = GradientDrawable()
        gradientDrawable.cornerRadius = resources.getDimension(R.dimen.button_corner_radius)
        gradientDrawable.setStroke(0, ContextCompat.getColor(this, R.color.yellow))
        gradientDrawable.setColor(ContextCompat.getColor(this, R.color.white))

        button.background = gradientDrawable
    }

    private fun clearPreviousSelection(viewId: Int) {
        when (viewId) {
            R.id.daysScheduleGrid -> {
                for (button in daysButtons) {
                    clearButtonSelection(button)
                }
                for (button in sessionsButtons) {
                    clearButtonFill(button)
                }
            }
        }
    }

    private fun select(view: View, motionEvent: MotionEvent) {
        when (view.id) {
            R.id.daysScheduleGrid -> {
                //selectDayButton(motionEvent)
            }

            R.id.timeScheduleGrid -> {
                val buttonIndex = findTouchedButton(motionEvent, sessionsButtons) ?: return
                val button = sessionsButtons[buttonIndex]
                //selectTimeButton(button)
            }
        }
    }

    fun clearDay(dayIndex: Int) {
        clearButtonFill(daysButtons[dayIndex])
    }

    fun fillDay(dayIndex: Int) {
        fillButton(daysButtons[dayIndex])
    }

    private fun findTouchedButton(motionEvent: MotionEvent, buttons: List<Button>): Int? {
        for ((index, button) in buttons.withIndex()) {
            if (!isButtonTouched(button, motionEvent)) {
                continue
            }

            if (!button.isEnabled) {
                break
            }

            if (isAlreadyTouched(button)) {
                break
            }

            return index
        }

        return null
    }

    private fun isAlreadyTouched(button: Button): Boolean {
        return true //button.getTag(R.id.touchIdTag) == touchId
    }

    private fun isButtonSelected(button: Button): Boolean {
        return button.getTag(R.id.touchedTag) == TOUCHED
    }

    private fun selectButton(button: Button) {
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
    }

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

    private fun fillButtonInHalf(button: Button) {
        val a = button.getTag(R.id.touchedTag)
        val gradientDrawable = button.background as GradientDrawable

        gradientDrawable.setColor(ContextCompat.getColor(this, R.color.light_yellow))
        button.background = gradientDrawable

        button.setTag(R.id.touchedTag, NOT_TOUCHED)
    }

    private fun isButtonTouched(button: Button, motionEvent: MotionEvent): Boolean {
        return button.x < motionEvent.x &&
                button.x + button.width > motionEvent.x &&
                button.y < motionEvent.y &&
                button.y + button.height > motionEvent.y
    }

    companion object {
        private const val WEEK_DAY_COUNT = 7
        private const val WEEK_COUNT = 4
        private const val TIME_COLUMN_COUNT = 6
        private const val TIME_RAW_COUNT = 8

        private const val TOUCHED = "touched"
        private const val NOT_TOUCHED = "not touched"
    }
}