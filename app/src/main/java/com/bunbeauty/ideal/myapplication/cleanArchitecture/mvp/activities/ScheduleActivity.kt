package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.LinearLayout
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.schedule.ScheduleInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.CustomGridLayout
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.SchedulePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.ScheduleView
import javax.inject.Inject


class ScheduleActivity : MvpAppCompatActivity(), ScheduleView, View.OnTouchListener {

    private lateinit var daysLayout: CustomGridLayout
    private lateinit var timeLayout: CustomGridLayout
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
            .appModule(AppModule(application, intent))
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
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init() {
        daysLayout = findViewById(R.id.daysScheduleLayout)
        daysLayout.setOnTouchListener(this)
        timeLayout = findViewById(R.id.timeScheduleLayout)
        timeLayout.setOnTouchListener(this)
        timeLayout.visibility = View.GONE
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun createDaysButtons() {
        for (weekIndex in 0 until WEEK_COUNT) {
            for (weekDayIndex in 0 until WEEK_DAY_COUNT) {
                val button = Button(this)
                button.layoutParams = LinearLayout.LayoutParams(
                    getScreenWidth() / WEEK_DAY_COUNT,
                    getScreenWidth() / WEEK_DAY_COUNT
                )
                clearButtonSelection(button)
                button.text =
                    schedulePresenter.getDateString(weekIndex * WEEK_DAY_COUNT + weekDayIndex)
                button.setOnTouchListener(this)

                daysButtons.add(button)
            }
        }

        for (i in 0 until daysButtons.size) {
            addViewToContainer(daysButtons[i], daysLayout)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun createTimeButtons() {
        for (i in 0 until TIME_RAW_COUNT) {
            for (j in 0 until TIME_COLUMN_COUNT) {
                val button = Button(this)
                button.layoutParams = LinearLayout.LayoutParams(
                    getScreenWidth() / TIME_COLUMN_COUNT,
                    WRAP_CONTENT
                )
                clearButtonSelection(button)
                button.text = schedulePresenter.getTineString(i * TIME_COLUMN_COUNT + j)
                button.setOnTouchListener(this)

                timeButtons.add(button)
            }
        }

        for (i in 0 until timeButtons.size) {
            addViewToContainer(timeButtons[i], timeLayout)
        }
    }

    private fun getScreenWidth(): Int {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

    private fun addViewToContainer(view: View, container: ViewGroup) {
        if (view.parent != null) {
            (view.parent as ViewGroup).removeView(view)
        }
        container.addView(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {

        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                touchId++
                clearSelection(view, daysButtons)
            }
            MotionEvent.ACTION_MOVE -> {
                select(view, motionEvent)
            }
            MotionEvent.ACTION_UP -> {
                timeLayout.visibility = View.VISIBLE
                clearSelection(view, timeButtons)
            }
        }

        return true
    }

    private fun clearSelection(view: View, buttons: List<Button>) {
        when (view) {
            daysLayout -> {
                clearButtonsSelection(buttons)
            }
        }
    }

    private fun select(view: View, motionEvent: MotionEvent) {
        when (view) {
            daysLayout -> {
                selectButton(motionEvent, daysButtons)
            }

            timeLayout -> {
                selectButton(motionEvent, timeButtons)
            }
        }
    }

    private fun selectButton(motionEvent: MotionEvent, buttons: List<Button>) {
        for (button in buttons) {
            if (!isButtonTouched(button, motionEvent)) {
                continue
            }

            if (isAlreadyTouched(button)) {
                return
            }

            button.setTag(R.id.touchIdTag, touchId)
            if (isButtonSelected(button)) {
                clearButtonSelection(button)
            } else {
                selectButton(button)
            }

            return
        }
    }

    private fun clearButtonsSelection(buttons: List<Button>) {
        for (button in buttons) {
            clearButtonSelection(button)
        }
    }

    private fun isAlreadyTouched(button: Button): Boolean {
        return button.getTag(R.id.touchIdTag) == touchId
    }

    private fun isButtonSelected(button: Button): Boolean {
        val a = button.getTag(R.id.touchedTag)

        return button.getTag(R.id.touchedTag) == TOUCHED
    }

    private fun selectButton(button: Button) {
        button.setBackgroundResource(R.drawable.yellow_button)
        button.setTag(R.id.touchedTag, TOUCHED)
    }

    private fun clearButtonSelection(button: Button) {
        button.setBackgroundResource(R.drawable.white_button)
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