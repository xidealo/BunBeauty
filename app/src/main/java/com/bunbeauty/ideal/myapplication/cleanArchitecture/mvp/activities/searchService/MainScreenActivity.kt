package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.searchService

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.paris.extensions.style
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.ServiceAdapter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService.MainScreenDataInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService.MainScreenServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService.MainScreenUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.MainScreenData
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.FirebaseModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.InteractorModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.enums.ButtonTask
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.IBottomPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.ITopPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.MainScreenPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.MainScreenView
import com.google.android.material.button.MaterialButton
import com.miguelcatalan.materialsearchview.MaterialSearchView
import kotlinx.android.synthetic.main.activity_main_screen.*
import kotlinx.android.synthetic.main.fragment_category.*
import kotlinx.android.synthetic.main.part_top_panel.*
import javax.inject.Inject

class MainScreenActivity : MvpAppCompatActivity(), View.OnClickListener, MainScreenView,
    ITopPanel, IBottomPanel {

    private var categoriesBtns: ArrayList<MaterialButton> = arrayListOf()
    private lateinit var categories: ArrayList<String>
    private lateinit var serviceAdapter: ServiceAdapter

    override var panelContext: Activity = this

    @InjectPresenter
    lateinit var mainScreenPresenter: MainScreenPresenter

    @Inject
    lateinit var mainScreenUserInteractor: MainScreenUserInteractor

    @Inject
    lateinit var mainScreenServiceInteractor: MainScreenServiceInteractor

    @Inject
    lateinit var mainScreenDataInteractor: MainScreenDataInteractor

    @ProvidePresenter
    internal fun provideAddingServicePresenter(): MainScreenPresenter {
        DaggerAppComponent.builder()
            .appModule(AppModule(application))
            .firebaseModule(FirebaseModule())
            .interactorModule(InteractorModule(intent))
            .build()
            .inject(this)

        return MainScreenPresenter(
            mainScreenUserInteractor,
            mainScreenServiceInteractor,
            mainScreenDataInteractor
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen)

        init()
        hideSearchPanel()
        hideTags()

        initTopPanel("BunBeauty", ButtonTask.SEARCH)
        createSearchPanel()

        createMainScreen()
        setSupportActionBar(topPanel)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        val item: MenuItem = menu.findItem(R.id.action_search)
        searchPanelMainScreenSearchView.setMenuItem(item)
        return true
    }

    override fun onResume() {
        super.onResume()

        initBottomPanel(R.id.navigation_main)
    }

    private fun init() {
        categories = ArrayList(listOf(*resources.getStringArray(R.array.categories)))

        noResultMainScreenText.visibility = View.GONE

        val minimizeTagsBtn = findViewById<MaterialButton>(R.id.minimizeTagsMainScreenBtn)
        val clearTagsBtn = findViewById<MaterialButton>(R.id.clearTagsMainScreenBtn)

        resultsMainScreenRecycleView.layoutManager = LinearLayoutManager(this)

        serviceAdapter = ServiceAdapter(mainScreenPresenter.getMainScreenDataLink())
        resultsMainScreenRecycleView.adapter = serviceAdapter

        searchPanelMainScreenSearchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                //Do some magic
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                //Do some magic
                mainScreenPresenter.getMainScreenDataByName(newText)
                return false
            }
        })

        searchPanelMainScreenSearchView.setOnSearchViewListener(object : MaterialSearchView.SearchViewListener {
            override fun onSearchViewShown() {
                //Do some magic
                val k = 21
            }

            override fun onSearchViewClosed() {
                //Do some magic
                val k = 21
            }
        })

        minimizeTagsBtn.setOnClickListener(this)
        clearTagsBtn.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.minimizeTagsMainScreenBtn -> hideTags()

            R.id.clearTagsMainScreenBtn -> {
                mainScreenPresenter.clearCategory(categoriesBtns)
                mainScreenPresenter.showCurrentMainScreen()
            }

            else -> if ((v.parent as View).id == R.id.categoryMainScreenLayout) {
                //показать тэги
                //начать поиск по категории
                val category = (v as Button).text.toString()
                mainScreenPresenter.disableCategoryBtns(categoriesBtns)
                if (mainScreenPresenter.isSelectedCategory(category)) {
                    mainScreenPresenter.showCurrentMainScreen()
                    mainScreenPresenter.setTagsState(tagsMainScreenLayout.visibility)
                } else {
                    mainScreenPresenter.createMainScreenWithCategory(category)
                    enableCategoryButton(v)
                }
            } else {
                mainScreenPresenter.createMainScreenWithTag(v as TextView)
            }
        }
    }

    override fun createMainScreen() {
        mainScreenPresenter.createMainScreen()
    }

    override fun createSearchPanel() {

    }

    override fun actionClick() {
        showSearchPanel()
    }

    override fun showSearchPanel() {
    }

    override fun hideSearchPanel() {
    }

    override fun disableCategoryBtn(button: Button) {
        button.setBackgroundResource(R.drawable.category_button)
        button.setTextColor(Color.WHITE)
    }

    override fun enableTag(tagText: TextView) {
        tagText.setBackgroundResource(R.drawable.category_button_pressed)
        tagText.setTextColor(Color.BLACK)
        val txt = tagText.text.toString()
    }

    override fun disableTag(tagText: TextView) {
        tagText.setBackgroundResource(R.drawable.block_text)
        tagText.setTextColor(Color.GRAY)
    }

    override fun showLoading() {
        progressBarMainScreen.visibility = View.VISIBLE
        resultsMainScreenRecycleView.visibility = View.GONE
    }

    override fun hideLoading() {
        progressBarMainScreen.visibility = View.GONE
        resultsMainScreenRecycleView.visibility = View.VISIBLE
    }

    override fun showMainScreen(mainScreenData: ArrayList<MainScreenData>) {
        serviceAdapter.notifyDataSetChanged()
    }

    override fun showMainScreenByUserName(city: String, name: String) {
        mainScreenPresenter.createMainScreenWithSearchUserName(city, name)
    }

    override fun showMainScreenByServiceName(city: String, serviceName: String) {
        mainScreenPresenter.createMainScreenWithSearchServiceName(city, serviceName)
    }

    override fun hideTags() {
        tagsMainScreenLayout.visibility = View.GONE
    }

    override fun clearTags() {
        tagsInnerMainScreenLayout.removeAllViews()
    }

    override fun showTags() {
        tagsMainScreenLayout.visibility = View.VISIBLE
    }

    override fun showEmptyScreen() {
        noResultMainScreenText.visibility = View.VISIBLE
    }

    override fun enableCategoryButton(button: Button) {
        button.setBackgroundResource(R.drawable.category_button_pressed)
        button.setTextColor(Color.BLACK)
    }

    override fun createCategoryFeed(categories: MutableSet<String>) {
        for (i in categories.indices) {
            categoriesBtns.add(MaterialButton(this))
            categoriesBtns[i].setOnClickListener(this)
            categoriesBtns[i].text = categories.toTypedArray()[i]
            categoriesBtns[i].textSize = 14f
            categoriesBtns[i].setBackgroundResource(R.drawable.category_button)
            categoriesBtns[i].setTextColor(Color.WHITE)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(8, 8, 8, 8)
            categoriesBtns[i].layoutParams = params

            categoryMainScreenLayout.addView(categoriesBtns[i])
        }
    }

    override fun showCategory() {
        categoryMainScreenLayout.visibility = View.VISIBLE
    }

    override fun hideCategory() {
        categoryMainScreenLayout.visibility = View.GONE
    }

    override fun createTags(category: String, selectedTagsArray: ArrayList<String>) {
        val tagsArray = resources
            .obtainTypedArray(R.array.tags_references)
            .getTextArray(categories.indexOf(category))

        for (tag in tagsArray) {
            val inflater = LayoutInflater.from(this)
            val view: View = inflater.inflate(R.layout.fragment_tag, tagsMaxLayout, false)
            view.layoutParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

            val tagText = view.findViewById<TextView>(R.id.tagFragmentTagText)
            tagText.text = tag
            tagText.setOnClickListener(this)

            if (selectedTagsArray.contains(tag.toString())) {
                view.style(R.style.selected)
            }

            tagsInnerMainScreenLayout.addView(view)

        }
    }
}
