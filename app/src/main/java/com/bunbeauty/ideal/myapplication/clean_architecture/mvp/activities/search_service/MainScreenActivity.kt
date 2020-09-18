package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.search_service

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.paris.extensions.style
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.MainScreenData
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.search_service.MainScreenDataInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.search_service.MainScreenServiceInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.search_service.MainScreenUserInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.enums.ButtonTask
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.adapters.ServiceAdapter
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.base.BaseActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.MainScreenPresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.MainScreenView
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.miguelcatalan.materialsearchview.MaterialSearchView
import kotlinx.android.synthetic.main.activity_main_screen.*
import kotlinx.android.synthetic.main.fragment_category_block.*
import kotlinx.android.synthetic.main.fragment_tag.*
import kotlinx.android.synthetic.main.fragment_tag.view.*
import kotlinx.android.synthetic.main.part_top_panel.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class MainScreenActivity : BaseActivity(), View.OnClickListener, MainScreenView {

    private var categoriesButtonList: MutableList<MaterialButton> = ArrayList()
    private lateinit var categories: ArrayList<String>

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
        buildDagger().inject(this)

        return MainScreenPresenter(
            mainScreenUserInteractor,
            mainScreenServiceInteractor,
            mainScreenDataInteractor,
            intent
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
            }

            override fun onSearchViewClosed() {

            }
        })
    }

    override fun onClick(view: View) {
        if ((view.parent as View).id == R.id.activity_main_screen_ll_category) {
            val category = (view as Button).text.toString()
            mainScreenPresenter.disableCategoryButtons(categoriesButtonList)
            if (mainScreenPresenter.isSelectedCategory(category)) {
                mainScreenPresenter.showCurrentMainScreen()
                mainScreenPresenter.setTagsState(activity_main_screen_ll_main_tags.visibility)
            } else {
                mainScreenPresenter.createMainScreenWithCategory(category)
                enableCategoryButton(view)
            }
        }
    }

    override fun createMainScreen() {
        mainScreenPresenter.createMainScreen()
    }

    override fun actionClick() {}

    override fun disableCategoryBtn(button: Button) {
        button.style(R.style.unselected_button)
    }

    override fun enableCategoryButton(button: Button) {
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
            val button = MaterialButton(this)
            button.setOnClickListener(this)
            button.text = categories.toTypedArray()[i]
            button.style(R.style.unselected_button)
            button.layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
                setMargins(8, 8, 8, 8)
            }

            activity_main_screen_ll_category.addView(button)
            categoriesButtonList.add(button)
        }
    }

    override fun createTags(category: String, selectedTagsArray: ArrayList<String>) {
        val tagsArray = resources
            .obtainTypedArray(R.array.tags_references)
            .getTextArray(categories.indexOf(category))

        for (tag in tagsArray) {
            val inflater = LayoutInflater.from(this)
            val tagView = inflater.inflate(R.layout.fragment_tag, tagsMaxLayout, false)
            tagView.layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            tagView.tagFragmentTagChip.text = tag
            tagView.tagFragmentTagChip.setOnClickListener {
                mainScreenPresenter.createMainScreenWithTag(it as Chip)
            }

            if (selectedTagsArray.contains(tag.toString())) {
                tagView.style(R.style.choiceChip)
            }

            activity_main_screen_ll_tags.addView(tagView)
        }
    }
}
