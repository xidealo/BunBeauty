package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.search_service

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
import com.bunbeauty.ideal.myapplication.clean_architecture.adapters.ServiceAdapter
import com.bunbeauty.ideal.myapplication.clean_architecture.business.search_service.MainScreenDataInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.business.search_service.MainScreenServiceInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.business.search_service.MainScreenUserInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.MainScreenData
import com.bunbeauty.ideal.myapplication.clean_architecture.di.component.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.clean_architecture.di.module.AdapterModule
import com.bunbeauty.ideal.myapplication.clean_architecture.di.module.AppModule
import com.bunbeauty.ideal.myapplication.clean_architecture.di.module.FirebaseModule
import com.bunbeauty.ideal.myapplication.clean_architecture.di.module.InteractorModule
import com.bunbeauty.ideal.myapplication.clean_architecture.enums.ButtonTask
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.interfaces.IBottomPanel
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.interfaces.ITopPanel
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.MainScreenPresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.MainScreenView
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

    override var panelContext: Activity = this

    @InjectPresenter
    lateinit var mainScreenPresenter: MainScreenPresenter

    @Inject
    lateinit var mainScreenUserInteractor: MainScreenUserInteractor

    @Inject
    lateinit var mainScreenServiceInteractor: MainScreenServiceInteractor

    @Inject
    lateinit var mainScreenDataInteractor: MainScreenDataInteractor

    @Inject
    lateinit var serviceAdapter: ServiceAdapter

    @ProvidePresenter
    internal fun provideAddingServicePresenter(): MainScreenPresenter {
        DaggerAppComponent.builder()
            .appModule(AppModule(application))
            .firebaseModule(FirebaseModule())
            .interactorModule(InteractorModule(intent))
            .adapterModule(AdapterModule())
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
        setSupportActionBar(top_panel)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        val item: MenuItem = menu.findItem(R.id.action_search)
        activity_main_screen_msv_search.setMenuItem(item)
        return true
    }

    override fun onResume() {
        super.onResume()

        initBottomPanel(R.id.navigation_main)
    }

    private fun init() {
        categories = ArrayList(listOf(*resources.getStringArray(R.array.categories)))

        activity_main_screen_tv_empty.visibility = View.GONE

        val minimizeTagsBtn = findViewById<MaterialButton>(R.id.activity_main_screen_btn_close)
        val clearTagsBtn = findViewById<MaterialButton>(R.id.activity_main_screen_btn_clear)

        activity_main_screen_rv_services.layoutManager = LinearLayoutManager(this)

        activity_main_screen_rv_services.adapter = serviceAdapter

        activity_main_screen_msv_search.setOnQueryTextListener(object :
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

        activity_main_screen_msv_search.setOnSearchViewListener(object :
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
            R.id.activity_main_screen_btn_close -> hideTags()

            R.id.activity_main_screen_btn_clear -> {
                mainScreenPresenter.clearCategory(categoriesBtns)
                mainScreenPresenter.showCurrentMainScreen()
            }

            else -> if ((v.parent as View).id == R.id.activity_main_screen_ll_category) {
                //показать тэги
                //начать поиск по категории
                val category = (v as Button).text.toString()
                mainScreenPresenter.disableCategoryBtns(categoriesBtns)
                if (mainScreenPresenter.isSelectedCategory(category)) {
                    mainScreenPresenter.showCurrentMainScreen()
                    mainScreenPresenter.setTagsState(activity_main_screen_ll_main_tags.visibility)
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
        activity_main_screen_pb_loading.visibility = View.VISIBLE
        activity_main_screen_rv_services.visibility = View.GONE
    }

    override fun hideLoading() {
        activity_main_screen_pb_loading.visibility = View.GONE
        activity_main_screen_rv_services.visibility = View.VISIBLE
    }

    override fun showMainScreen(mainScreenData: ArrayList<MainScreenData>) {
        serviceAdapter.setData(mainScreenData)
    }

    override fun hideTags() {
        activity_main_screen_ll_main_tags.visibility = View.GONE
    }

    override fun clearTags() {
        activity_main_screen_ll_tags.removeAllViews()
    }

    override fun showTags() {
        activity_main_screen_ll_main_tags.visibility = View.VISIBLE
    }

    override fun showEmptyScreen() {
        activity_main_screen_tv_empty.visibility = View.VISIBLE
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

            activity_main_screen_ll_category.addView(categoriesBtns[i])
        }
    }

    override fun showCategory() {
        activity_main_screen_ll_category.visibility = View.VISIBLE
    }

    override fun hideCategory() {
        activity_main_screen_ll_category.visibility = View.GONE
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

            activity_main_screen_ll_tags.addView(view)
        }
    }
}
