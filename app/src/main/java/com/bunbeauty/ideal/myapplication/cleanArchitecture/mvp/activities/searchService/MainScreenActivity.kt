package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.searchService

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
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
import com.google.android.material.chip.Chip
import com.miguelcatalan.materialsearchview.MaterialSearchView
import kotlinx.android.synthetic.main.activity_main_screen.*
import kotlinx.android.synthetic.main.fragment_category_block.*
import kotlinx.android.synthetic.main.part_top_panel.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

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
        hideTags()

        initTopPanel("BunBeauty", ButtonTask.SEARCH)

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

        searchPanelMainScreenSearchView.setOnQueryTextListener(object :
            MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                mainScreenPresenter.getMainScreenDataByName(query?.toLowerCase(Locale.ROOT))
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                //Do some magic
                mainScreenPresenter.getMainScreenDataByName(newText?.toLowerCase(Locale.ROOT))
                return false
            }
        })

        searchPanelMainScreenSearchView.setOnSearchViewListener(object :
            MaterialSearchView.SearchViewListener {
            override fun onSearchViewShown() {
                //Do some magic
                val k = 21
            }

            override fun onSearchViewClosed() {

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
                mainScreenPresenter.createMainScreenWithTag(v as Chip)
            }
        }
    }

    override fun createMainScreen() {
        mainScreenPresenter.createMainScreen()
    }

    override fun actionClick() {

    }

    override fun disableCategoryBtn(button: Button) {
        button.setTextColor(Color.WHITE)
        button.style(R.style.unselected_button)
    }

    override fun enableCategoryButton(button: Button) {
        button.setTextColor(Color.BLACK)
        button.style(R.style.selected_button)
    }

    override fun enableTag(tagText: Chip) {
        tagText.chipBackgroundColor =
            ColorStateList.valueOf(ContextCompat.getColor(this, R.color.yellow))
        tagText.setTextColor(ContextCompat.getColor(this, R.color.black))
    }

    override fun disableTag(tagText: Chip) {
        tagText.chipBackgroundColor =
            ColorStateList.valueOf(ContextCompat.getColor(this, R.color.mainBlue))
        tagText.setTextColor(ContextCompat.getColor(this, R.color.white))
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

    override fun createCategoryFeed(categories: MutableSet<String>) {
        for (i in categories.indices) {
            categoriesBtns.add(MaterialButton(this))
            categoriesBtns[i].setOnClickListener(this)
            categoriesBtns[i].text = categories.toTypedArray()[i]
            categoriesBtns[i].style(R.style.unselected_button)
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

            val tagText = view.findViewById<Chip>(R.id.tagFragmentTagChip)
            tagText.text = tag
            tagText.setOnClickListener(this)

            if (selectedTagsArray.contains(tag.toString())) {
                view.style(R.style.choiceChip)
            }

            tagsInnerMainScreenLayout.addView(view)
        }
    }
}
