package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.schedule.WorkingTime
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.api.gone
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.api.invisible
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.api.visible
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.schedule.ScheduleInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.base.BaseActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.SchedulePresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.ScheduleView
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.activity_schedule.*
import javax.inject.Inject

class ScheduleActivity : BaseActivity(), ScheduleView, View.OnTouchListener {

    override var panelContext: Activity = this

    private val daysButtons = ArrayList<MaterialButton>()
    private val timeButtons = ArrayList<MaterialButton>()

    private var touchId = 0

    @Inject
    lateinit var scheduleInteractor: ScheduleInteractor

    @InjectPresenter
    lateinit var schedulePresenter: SchedulePresenter

    @ProvidePresenter
    internal fun provideSchedulePresenter(): SchedulePresenter {
        buildDagger().inject(this)
        return SchedulePresenter(scheduleInteractor)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        init()
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
        activity_schedule_gl_days.setOnTouchListener(this)
        activity_schedule_gl_time.setOnTouchListener(this)
        activity_schedule_ll_time.gone()
        activity_schedule_gl_days.gone()
        activity_schedule_btn_save.gone()
    }

    private fun createTimeButtons() {
        for (i in 0 until TIME_RAW_COUNT) {
            for (j in 0 until TIME_COLUMN_COUNT) {
                val width = getScreenWidth() / TIME_COLUMN_COUNT
                val height = resources.getDimensionPixelSize(R.dimen.schedule_time_button_height)
                val text = WorkingTime.getTimeByIndex(i * TIME_COLUMN_COUNT + j)
                val strokeColor =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.yellow))
                val button = createButton(width, height, text, strokeColor)

                timeButtons.add(button)
            }
        }

        for (i in 0 until timeButtons.size) {
            addViewToContainer(timeButtons[i], activity_schedule_gl_time)
        }
    }

    private fun getScreenWidth(): Int {
        return resources.displayMetrics.widthPixels
    }

    override fun showSchedule(dayIndexes: Set<Int>) {
        createDaysButtons(dayIndexes)

        activity_schedule_pb_loading.gone()
        activity_schedule_gl_days.visible()
        activity_schedule_btn_save.visible()
        activity_schedule_btn_save.setOnClickListener {
            schedulePresenter.saveSchedule()
        }
    }

    private fun createDaysButtons(selectedDayIndexes: Set<Int>) {
        for (weekIndex in 0 until WEEK_COUNT) {
            for (weekDayIndex in 0 until WEEK_DAY_COUNT) {
                val width = getScreenWidth() / WEEK_DAY_COUNT
                val height = width
                val text =
                    schedulePresenter.getStringDate(weekIndex * WEEK_DAY_COUNT + weekDayIndex)
                val strokeColor =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.mainBlue))
                val button = createButton(width, height, text, strokeColor)

                setButtonEnable(button, weekIndex * WEEK_DAY_COUNT + weekDayIndex)
                setButtonSelection(
                    button,
                    weekIndex * WEEK_DAY_COUNT + weekDayIndex,
                    selectedDayIndexes
                )

                daysButtons.add(button)
            }
        }

        for (button in daysButtons) {
            addViewToContainer(button, activity_schedule_gl_days)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setButtonEnable(button: MaterialButton, dayIndex: Int) {
        if (schedulePresenter.isPastDay(dayIndex)) {
            button.invisible()
            button.isEnabled = false
        } else {
            button.setOnTouchListener(this)
        }
    }

    private fun setButtonSelection(
        button: MaterialButton,
        dayIndex: Int,
        selectedDayIndexes: Set<Int>
    ) {
        if (selectedDayIndexes.contains(dayIndex)) {
            showDayButtonFill(button)
        }
    }

    @SuppressLint("InflateParams")
    private fun createButton(
        width: Int,
        height: Int,
        text: String,
        strokeColor: ColorStateList
    ): MaterialButton {
        val button = LayoutInflater.from(this).inflate(R.layout.element_schedule_button, null) as MaterialButton

        val margin = resources.getDimensionPixelSize(R.dimen.schedule_button_margin)
        button.layoutParams = LinearLayout.LayoutParams(width - 2 * margin, height).apply {
            setMargins(margin, 0, margin, 0)
        }
        button.strokeColor = strokeColor
        button.text = text

        return button
    }

    private fun addViewToContainer(view: View, container: ViewGroup) {
        if (view.parent != null) {
            (view.parent as ViewGroup).removeView(view)
        }
        container.addView(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {

        if (schedulePresenter.hasSomeSelectedDays()) {
            activity_schedule_ll_time.visible()
        } else {
            activity_schedule_ll_time.gone()
        }

        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                clearPreviousSelection(view.id)
                if (view.id == R.id.activity_schedule_gl_days) {
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
            R.id.activity_schedule_gl_days -> {
                for (button in daysButtons) {
                    hideDayButtonStroke(button)
                }
                for (button in timeButtons) {
                    button.isEnabled = true
                    clearTimeButtonFill(button)
                }
            }
        }
    }

    private fun select(view: View, motionEvent: MotionEvent) {
        when (view.id) {
            R.id.activity_schedule_gl_days -> {
                selectDayButton(motionEvent)
            }

            R.id.activity_schedule_gl_time -> {
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
        showDayButtonStroke(button)
        schedulePresenter.rememberDay(buttonIndex, button.text.toString())
    }

    override fun showAccurateTime(accurateTimeSet: Set<String>) {
        timeButtons.filter {
            accurateTimeSet.contains(it.text.toString())
        }.map {
            it.isEnabled = true
            it.setTag(R.id.touchIdTag, touchId)
            showTimeButtonFill(it)
        }
    }

    override fun showTimeWithOrder(timeWithOrderSet: Set<String>) {
        timeButtons.filter {
            timeWithOrderSet.contains(it.text.toString())
        }.map {
            it.isEnabled = false
        }
    }

    override fun showInaccurateTime(inaccurateTimeSet: Set<String>) {
        timeButtons.filter {
            inaccurateTimeSet.contains(it.text.toString())
        }.map {
            it.isEnabled = true
            showTimeButtonStroke(it)
        }
    }

    private fun selectTimeButton(button: MaterialButton) {
        button.setTag(R.id.touchIdTag, touchId)
        if (isButtonSelected(button)) {
            val selectedDayTexts =
                schedulePresenter.getSelectedDays().map { daysButtons[it].text.toString() }
            schedulePresenter.deleteFromSchedule(selectedDayTexts, button.text.toString())
        } else {
            showTimeButtonFill(button)

            val selectedDayList =
                schedulePresenter.getSelectedDays().map { daysButtons[it].text.toString() }
            schedulePresenter.addToSchedule(selectedDayList, button.text.toString())
        }
    }

    override fun clearTime(timeString: String) {
        clearDayButtonFill(timeButtons.find { it.text == timeString }!!)
    }

    override fun clearDay(dayIndex: Int) {
        clearDayButtonFill(daysButtons[dayIndex])
    }

    override fun fillDay(dayIndex: Int) {
        showDayButtonFill(daysButtons[dayIndex])
    }

    private fun findTouchedButton(motionEvent: MotionEvent, buttons: List<MaterialButton>): Int? {
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

    private fun isAlreadyTouched(button: MaterialButton): Boolean {
        return button.getTag(R.id.touchIdTag) == touchId
    }

    private fun isButtonSelected(button: MaterialButton): Boolean {
        return button.getTag(R.id.touchedTag) == TOUCHED
    }

    private fun showDayButtonStroke(button: MaterialButton) {
        button.strokeWidth = 4

        button.setTag(R.id.touchedTag, TOUCHED)
    }

    private fun hideDayButtonStroke(button: MaterialButton) {
        button.strokeWidth = 0

        button.setTag(R.id.touchedTag, NOT_TOUCHED)
    }

    private fun showDayButtonFill(button: MaterialButton) {
        button.setBackgroundColor(ContextCompat.getColor(this, R.color.yellow))

        button.setTag(R.id.touchedTag, TOUCHED)
    }

    private fun clearDayButtonFill(button: MaterialButton) {
        button.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

        button.setTag(R.id.touchedTag, NOT_TOUCHED)
    }

    private fun showTimeButtonStroke(button: MaterialButton) {
        button.strokeWidth = 10
        button.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

        button.setTag(R.id.touchedTag, NOT_TOUCHED)
    }

    private fun showTimeButtonFill(button: MaterialButton) {
        button.strokeWidth = 0
        button.setBackgroundColor(ContextCompat.getColor(this, R.color.yellow))

        button.setTag(R.id.touchedTag, TOUCHED)
    }

    private fun clearTimeButtonFill(button: MaterialButton) {
        button.strokeWidth = 0
        button.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

        button.setTag(R.id.touchedTag, NOT_TOUCHED)
    }

    private fun isButtonTouched(button: MaterialButton, motionEvent: MotionEvent): Boolean {
        return button.x < motionEvent.x &&
                button.x + button.width > motionEvent.x &&
                button.y < motionEvent.y &&
                button.y + button.height > motionEvent.y
    }

    override fun showMessage(message: String) {
        showMessage(message, activity_schedule_ll_main)
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