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
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.fragments.SearchServiceFragment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.IBottomPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.ITopMainScreenPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.fragments.general.TopPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.MainScreenPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.MainScreenView
import javax.inject.Inject

class MainScreenActivity : MvpAppCompatActivity(), View.OnClickListener, MainScreenView,
        ITopMainScreenPanel, IBottomPanel {


    private var categoriesBtns: ArrayList<Button> = arrayListOf()
    private lateinit var categories: ArrayList<String>
    private lateinit var categoryLayout: LinearLayout
    private lateinit var tagsLayout: LinearLayout
    private lateinit var innerLayout: LinearLayout
    private lateinit var searchLayout: LinearLayout
    private lateinit var headerLayout: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var serviceAdapter: ServiceAdapter

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
        createTopPanel()
        createBottomPanel(supportFragmentManager, R.id.bottomMainScreenLayout)
        createSearchPanel()
        createMainScreen()
    }

    private fun init() {
        categories = ArrayList(listOf(*resources.getStringArray(R.array.categories)))

        categoryLayout = findViewById(R.id.categoryMainScreenLayout)
        recyclerView = findViewById(R.id.resultsMainScreenRecycleView)
        categoryLayout = findViewById(R.id.categoryMainScreenLayout)
        tagsLayout = findViewById(R.id.tagsMainScreenLayout)
        innerLayout = findViewById(R.id.tagsInnerMainScreenLayout)
        progressBar = findViewById(R.id.progressBarMainScreen)
        searchLayout = findViewById(R.id.searchMainScreenLayout)
        headerLayout = findViewById(R.id.topMainScreenLayout)

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

    override fun onClick(v: View) {
        when (v.id) {
            R.id.minimizeTagsMainScreenBtn -> hideTags()

            R.id.clearTagsMainScreenBtn -> {
                mainScreenPresenter.clearCategory(categoriesBtns)
                mainScreenPresenter.createMainScreen()
            }

            else -> if ((v.parent as View).id == R.id.categoryMainScreenLayout) {
                //показать тэги
                //начать поиск по категории
                val category = (v as Button).text.toString()
                mainScreenPresenter.disableCategoryBtns(categoriesBtns)
                if (mainScreenPresenter.isSelectedCategory(category)) {
                    mainScreenPresenter.createMainScreen()
                    mainScreenPresenter.setTagsState(tagsLayout.visibility)
                } else {
                    mainScreenPresenter.createMainScreenWithCategory(category)
                    enableCategoryButton(v)
                }
            } else {
                mainScreenPresenter.createMainScreenWithTag(v as TextView)
            }
        }
    }

    override fun createTopPanel() {
        val topPanel = TopPanel()

        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.topMainScreenLayout, topPanel)
        transaction.commit()
    }

    override fun showTopPanel() {
        headerLayout.visibility = View.VISIBLE
    }

    override fun hideTopPanel() {
        headerLayout.visibility = View.GONE
    }

    override fun createMainScreen() {
        mainScreenPresenter.createMainScreen()
    }

    override fun createSearchPanel() {
        val searchServiceFragment = SearchServiceFragment()

        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.searchMainScreenLayout, searchServiceFragment)
        transaction.commit()
    }

    override fun showSearchPanel() {
        searchLayout.visibility = View.VISIBLE
    }

    override fun hideSearchPanel() {
        searchLayout.visibility = View.GONE
    }

    override fun disableCategoryBtn(button: Button) {
        button.setBackgroundResource(R.drawable.category_button)
        button.setTextColor(Color.WHITE)
    }

    override fun enableTag(tagText: TextView) {
        tagText.setBackgroundResource(R.drawable.category_button_pressed)
        tagText.setTextColor(Color.BLACK)
    }

    override fun disableTag(tagText: TextView) {
        tagText.setBackgroundResource(0)
        tagText.setTextColor(Color.GRAY)
    }

    override fun showLoading() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
    }

    override fun hideLoading() {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
    }

    override fun showMainScreen(mainScreenData: ArrayList<ArrayList<Any>>) {
        serviceAdapter = ServiceAdapter(mainScreenData.size, mainScreenData)
        recyclerView.adapter = serviceAdapter
    }

    override fun showMainScreenByUserName(city:String, name: String) {
        mainScreenPresenter.createMainScreenWithSearchUserName(city, name)
    }

    override fun showMainScreenByServiceName(city:String, serviceName: String) {
        mainScreenPresenter.createMainScreenWithSearchServiceName(city, serviceName)
    }

    override fun hideTags() {
        innerLayout.removeAllViews()
        tagsLayout.visibility = View.GONE
    }

    override fun showTags() {
        tagsLayout.visibility = View.VISIBLE
    }

    override fun enableCategoryButton(button: Button) {
        button.setBackgroundResource(R.drawable.category_button_pressed)
        button.setTextColor(resources.getColor(R.color.black))
    }

    // настроить вид кнопок
    override fun createCategoryFeed(categories: MutableSet<String>) {
        val width = resources.getDimensionPixelSize(R.dimen.categories_width)
        val height = resources.getDimensionPixelSize(R.dimen.categories_height)
        for (i in categories.indices) {
            categoriesBtns.add(Button(this))
            categoriesBtns[i].setOnClickListener(this)
            categoriesBtns[i].text = categories.toTypedArray()[i]
            categoriesBtns[i].textSize = 14f
            categoriesBtns[i].setBackgroundResource(R.drawable.category_button)
            categoriesBtns[i].setTextColor(Color.WHITE)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                categoriesBtns[i].setAutoSizeTextTypeUniformWithConfiguration(
                        8, 14, 1, TypedValue.COMPLEX_UNIT_DIP)
            }
            val params = LinearLayout.LayoutParams(
                    (width * categories.toTypedArray()[i].length / 6.6).toInt(),
                    height)
            params.setMargins(10, 10, 10, 16)
            categoriesBtns[i].layoutParams = params

            categoryLayout.addView(categoriesBtns[i])
        }
    }

    override fun showCategory() {
        categoryLayout.visibility = View.VISIBLE
    }

    override fun hideCategory() {
        categoryLayout.visibility = View.GONE
    }

    override fun createTags(category: String, selectedTagsArray: ArrayList<String>) {
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
    }

    companion object {
        // добавить, чтобы не было видно своих сервисов
        // например номер юзера, возвращаемого сервиса не должен быть равен локальному
        private val TAG = "DBInf"
    }
}
