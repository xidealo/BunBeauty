package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.schedule.ScheduleInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.ScheduleWithDays
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.schedule.WorkingTime
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.FirebaseModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.InteractorModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.enums.ButtonTask
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.IBottomPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.ITopPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.SchedulePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.ScheduleView
import kotlinx.android.synthetic.main.activity_schedule.*
import javax.inject.Inject

class ScheduleActivity : MvpAppCompatActivity(), ScheduleView, ITopPanel, IBottomPanel,
    View.OnTouchListener {

    override var panelContext: Activity = this

    private val daysButtons = ArrayList<Button>()
    private val timeButtons = ArrayList<Button>()

    private var touchId = 0

    @Inject
    lateinit var scheduleInteractor: ScheduleInteractor

    @InjectPresenter
    lateinit var schedulePresenter: SchedulePresenter

    @ProvidePresenter
    internal fun provideSchedulePresenter(): SchedulePresenter {
        DaggerAppComponent.builder()
            .appModule(AppModule(application))
            .firebaseModule(FirebaseModule())
            .interactorModule(InteractorModule(intent))
            .build()
            .inject(this)

        return SchedulePresenter(scheduleInteractor)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        init()
        createDaysButtons()
        createTimeButtons()

        initTopPanel("Расписание")
        schedulePresenter.getSchedule()
    }

    override fun onResume() {
        super.onResume()

        initBottomPanel()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init() {
        daysScheduleGrid.setOnTouchListener(this)
        timeScheduleGrid.setOnTouchListener(this)
        timeScheduleLayout.visibility = View.GONE
        saveScheduleButton.setOnClickListener {
            schedulePresenter.saveSchedule()
        }
    }

    private fun createDaysButtons() {
        for (weekIndex in 0 until WEEK_COUNT) {
            for (weekDayIndex in 0 until WEEK_DAY_COUNT) {
                val width = getScreenWidth() / WEEK_DAY_COUNT
                val height = resources.getDimensionPixelSize(R.dimen.schedule_button_height)
                val text =
                    schedulePresenter.getDateString(weekIndex * WEEK_DAY_COUNT + weekDayIndex)
                val button = getConfiguredButton(width, height, text)
                setButtonEnabled(button, weekIndex * WEEK_DAY_COUNT + weekDayIndex)

                daysButtons.add(button)
            }
        }

        for (i in 0 until daysButtons.size) {
            addViewToContainer(daysButtons[i], daysScheduleGrid)
        }
    }

    private fun setButtonEnabled(button: Button, dayIndex: Int) {
        button.isEnabled = schedulePresenter.isPastDay(dayIndex)
    }

    private fun createTimeButtons() {
        for (i in 0 until TIME_RAW_COUNT) {
            for (j in 0 until TIME_COLUMN_COUNT) {
                val width = getScreenWidth() / TIME_COLUMN_COUNT
                val height = resources.getDimensionPixelSize(R.dimen.schedule_button_height)
                val text = WorkingTime.getTimeByIndex(i * TIME_COLUMN_COUNT + j)
                val button = getConfiguredButton(width, height, text)

                timeButtons.add(button)
            }
        }

        for (i in 0 until timeButtons.size) {
            addViewToContainer(timeButtons[i], timeScheduleGrid)
        }
    }

    private fun getScreenWidth(): Int {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

    override fun setSchedule(schedule: ScheduleWithDays) {
        for (workingDay in schedule.workingDays) {
            val dayIndex = schedulePresenter.getDayIndex(workingDay.workingDay.date)
            if (dayIndex > 0 && dayIndex < daysButtons.size) {
                fillButton(daysButtons[dayIndex])
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun getConfiguredButton(width: Int, height: Int, text: String): Button {
        val button = Button(this)

        val margin = resources.getDimensionPixelSize(R.dimen.schedule_button_margin)
        val params = LinearLayout.LayoutParams(width - 2 * margin, height)
        params.setMargins(margin, margin, margin, margin)
        button.layoutParams = params
        setBackground(button)
        button.setTag(R.id.touchedTag, NOT_TOUCHED)
        button.text = text
        button.setOnTouchListener(this)

        return button
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

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                timeScheduleLayout.visibility = View.VISIBLE
                clearPreviousSelection(view.id)
                if (view.id == R.id.daysScheduleGrid) {
                    schedulePresenter.forgotAllDays()
                }

                touchId++
                select(view, motionEvent)
            }
            MotionEvent.ACTION_MOVE -> {
                select(view, motionEvent)
            }
        }

        return true
    }

    private fun clearPreviousSelection(viewId: Int) {
        when (viewId) {
            R.id.daysScheduleGrid -> {
                for (button in daysButtons) {
                    clearButtonSelection(button)
                }
                for (button in timeButtons) {
                    clearButtonFill(button)
                }
            }
        }
    }

    private fun select(view: View, motionEvent: MotionEvent) {
        when (view.id) {
            R.id.daysScheduleGrid -> {
                selectDayButton(motionEvent)
            }

            R.id.timeScheduleGrid -> {
                val buttonIndex = findTouchedButton(motionEvent, timeButtons) ?: return
                val button = timeButtons[buttonIndex]
                selectTimeButton(button)
            }
        }
    }

    private fun selectDayButton(motionEvent: MotionEvent) {
        val buttonIndex = findTouchedButton(motionEvent, daysButtons) ?: return
        val button = daysButtons[buttonIndex]

        button.setTag(R.id.touchIdTag, touchId)
        selectButton(button)
        schedulePresenter.rememberDay(buttonIndex, button.text.toString())
    }

    override fun showAccurateTime(accurateTime: Set<String>) {
        for (button in timeButtons) {
            if (accurateTime.contains(button.text.toString())) {
                button.setTag(R.id.touchIdTag, touchId)
                fillButton(button)
            }
        }
    }

    override fun showInaccurateTime(inaccurateTime: Set<String>) {
        for (button in timeButtons) {
            if (inaccurateTime.contains(button.text.toString())) {
                fillButtonInHalf(button)
            }
        }
    }

    private fun selectTimeButton(button: Button) {
        button.setTag(R.id.touchIdTag, touchId)
        if (isButtonSelected(button)) {
            clearButtonFill(button)

            val selectedDayTexts =
                schedulePresenter.getSelectedDays().map { daysButtons[it].text.toString() }
            schedulePresenter.removeFromSchedule(selectedDayTexts, button.text.toString())
        } else {
            fillButton(button)

            val selectedDayList =
                schedulePresenter.getSelectedDays().map { daysButtons[it].text.toString() }
            schedulePresenter.addToSchedule(selectedDayList, button.text.toString())
        }
    }

    override fun clearDay(dayIndex: Int) {
        clearButtonFill(daysButtons[dayIndex])
    }

    override fun fillDay(dayIndex: Int) {
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
        return button.getTag(R.id.touchIdTag) == touchId
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

    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun goBack() {
        onBackPressed()
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