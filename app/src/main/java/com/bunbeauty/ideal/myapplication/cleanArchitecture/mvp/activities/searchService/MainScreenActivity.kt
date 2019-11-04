package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.searchService

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.adapters.ServiceAdapter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService.MainScreenInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.MainScreenPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.MainScreenView
import com.bunbeauty.ideal.myapplication.helpApi.PanelBuilder
import com.bunbeauty.ideal.myapplication.helpApi.Search
import javax.inject.Inject

class MainScreenActivity : MvpAppCompatActivity(), View.OnClickListener, MainScreenView {

    private lateinit var search: Search

    private lateinit var categoriesBtns: Array<Button>
    private lateinit var categories: ArrayList<String>
    private lateinit var selectedTagsArray: ArrayList<String>
    private lateinit var categoryLayout: LinearLayout
    private lateinit var tagsLayout: LinearLayout
    private lateinit var innerLayout: LinearLayout
    private lateinit var progressBar: ProgressBar

    private lateinit var recyclerView: RecyclerView
    private lateinit var serviceAdapter: ServiceAdapter
    private var isUpdated: Boolean = false
    private lateinit var category: String

    @InjectPresenter
    lateinit var mainScreenPresenter: MainScreenPresenter
    @Inject
    lateinit var mainScreenInteractor: MainScreenInteractor

    @ProvidePresenter
    internal fun provideAddingServicePresenter(): MainScreenPresenter {
        DaggerAppComponent
                .builder()
                .appModule(AppModule(application, intent))
                .build().inject(this)

        return MainScreenPresenter(mainScreenInteractor)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_screen)

        init()
        showLoading()
        createCategoryFeed()
        createMainScreen()
    }

    private fun init() {
        search = Search(this)
        isUpdated = true
        categories = ArrayList(listOf(*resources.getStringArray(R.array.categories)))
        selectedTagsArray = ArrayList()
        categoriesBtns = arrayOf()

        categoryLayout = findViewById(R.id.categoryMainScreenLayout)
        recyclerView = findViewById(R.id.resultsMainScreenRecycleView)
        categoryLayout = findViewById(R.id.categoryMainScreenLayout)
        tagsLayout = findViewById(R.id.tagsMainScreenLayout)
        innerLayout = findViewById(R.id.tagsInnerMainScreenLayout)
        progressBar = findViewById(R.id.progressBarMainScreen)

        val minimizeTagsBtn = findViewById<Button>(R.id.minimizeTagsMainScreenBtn)
        val clearTagsBtn = findViewById<Button>(R.id.clearTagsMainScreenBtn)

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        /*recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (recyclerView.computeVerticalScrollOffset() == 0 && !isUpdated)
                {
                    serviceList.clear()
                    userList.clear()
                    progressBar.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                }
            }
        })*/

        minimizeTagsBtn.setOnClickListener(this)
        clearTagsBtn.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()

        val panelBuilder = PanelBuilder()
        panelBuilder.buildFooter(supportFragmentManager, R.id.footerMainScreenLayout)
        panelBuilder.buildHeader(supportFragmentManager, "Главная", R.id.headerMainScreenLayout)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.minimizeTagsMainScreenBtn -> hideTags()

            R.id.clearTagsMainScreenBtn -> {
                showLoading()
                clearCategory()
                createMainScreen()
            }

            else -> if ((v.parent as View).id == R.id.categoryMainScreenLayout) {
                categoriesClick(v as Button)
            } else {
                tagClick(v as TextView)
            }
        }
    }

    private fun tagClick(tagText: TextView) {
        showLoading()

        val text = tagText.text.toString()

        if (selectedTagsArray.contains(text)) {
            tagText.setBackgroundResource(0)
            tagText.setTextColor(Color.GRAY)
            selectedTagsArray.remove(text)
        } else {
            tagText.setBackgroundResource(R.drawable.category_button_pressed)
            tagText.setTextColor(Color.BLACK)
            selectedTagsArray.add(text)
        }

        createMainScreen()
    }

    override fun showLoading() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
    }

    override fun hideLoading() {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
    }

    override fun showMainScreen(serviceList: ArrayList<Service>, userList: ArrayList<User>){
        serviceAdapter = ServiceAdapter(serviceList.size, serviceList, userList)
        recyclerView.adapter = serviceAdapter
    }

    private fun categoriesClick(btn: Button) {
        // Если категория уже выбрана
        if (category == btn.text.toString()) {
            if (tagsLayout.visibility == View.VISIBLE) {
                hideTags()
            } else {
                createTags()
            }
        } else {
            showLoading()
            enableCategory(btn)
            category = btn.text.toString()
            mainScreenPresenter.createMainScreenWithCategory(category)
        }
    }

    override fun clearCategory() {
        for (btn in categoriesBtns) {
            if (category == btn.text.toString()) {
                category = ""
                disableCategoryBtn(btn)
                hideTags()
                selectedTagsArray.clear()
                break
            }
        }
    }

    override fun hideTags() {
        innerLayout.removeAllViews()
        tagsLayout.visibility = View.GONE
    }

    override fun showTags() {
        tagsLayout.visibility = View.VISIBLE
    }

    override fun enableCategory(button: Button) {
        button.setBackgroundResource(R.drawable.category_button_pressed)
        button.setTextColor(resources.getColor(R.color.black))
        hideTags()

        for (categoriesBtn in categoriesBtns) {
            if (category == categoriesBtn.text.toString()) {
                disableCategoryBtn(categoriesBtn)
                break
            }
        }
        selectedTagsArray.clear()
        category = button.text.toString()
        createTags()
    }

    override fun disableCategoryBtn(button: Button) {
        button.setBackgroundResource(R.drawable.category_button)
        button.setTextColor(Color.WHITE)
    }

    // настроить вид кнопок
    override fun createCategoryFeed() {
        val width = resources.getDimensionPixelSize(R.dimen.categories_width)
        val height = resources.getDimensionPixelSize(R.dimen.categories_height)
        for (i in categoriesBtns.indices) {
            categoriesBtns[i] = Button(this)
            categoriesBtns[i].setOnClickListener(this)
            categoriesBtns[i].text = categories[i]
            categoriesBtns[i].textSize = 14f
            disableCategoryBtn(categoriesBtns[i])
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                categoriesBtns[i].setAutoSizeTextTypeUniformWithConfiguration(
                        8, 14, 1, TypedValue.COMPLEX_UNIT_DIP)
            }

            val params = LinearLayout.LayoutParams(
                    (width * categories[i].length / 6.6).toInt(),
                    height)
            params.setMargins(10, 10, 10, 16)
            categoriesBtns[i].layoutParams = params

            categoryLayout.addView(categoriesBtns[i])
        }
    }

    private fun createMainScreen() {
        mainScreenPresenter.createMainScreen()
    }

    private fun getUserCity(userId: String): String {

      /*  val database = dbHelper.readableDatabase
        // Получить город юзера
        // Таблица Users
        // с фиксированным userId
        val sqlQuery = ("SELECT " + DBHelper.KEY_CITY_USERS
                + " FROM " + DBHelper.TABLE_CONTACTS_USERS
                + " WHERE " + DBHelper.KEY_ID + " = ?")

        val cursor = database.rawQuery(sqlQuery, arrayOf(userId))

        val indexCity = cursor.getColumnIndex(DBHelper.KEY_CITY_USERS)
        // дефолтное значение
        var city = "Dubna"

        if (cursor.moveToFirst()) {
            city = cursor.getString(indexCity)
        }
        cursor.close()
        return city*/
        return ""
    }

    override fun createTags() {
        val tagsArray = resources
                .obtainTypedArray(R.array.tags_references)
                .getTextArray(categories.indexOf(category))

        for (tag in tagsArray) {
            val tagText = TextView(this)
            tagText.text = tag.toString()
            tagText.setTextColor(Color.GRAY)
            tagText.gravity = Gravity.CENTER
            tagText.typeface = ResourcesCompat.getFont(this, R.font.roboto_bold)
            tagText.layoutParams = LinearLayout.LayoutParams(
                    700,
                    LinearLayout.LayoutParams.WRAP_CONTENT)
            tagText.setOnClickListener(this)
            tagText.setPadding(0, 16, 0, 16)
            if (selectedTagsArray.contains(tag.toString())) {
                tagText.setBackgroundResource(R.drawable.category_button_pressed)
                tagText.setTextColor(Color.BLACK)
            }
            innerLayout.addView(tagText)
        }
        showTags()
    }

    companion object {
        // добавить, чтобы не было видно своих сервисов
        // например номер юзера, возвращаемого сервиса не должен быть равен локальному
        private val TAG = "DBInf"
    }
}
