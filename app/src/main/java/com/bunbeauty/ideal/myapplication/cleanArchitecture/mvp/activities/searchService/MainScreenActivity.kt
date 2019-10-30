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
import com.bunbeauty.ideal.myapplication.adapters.ServiceAdapter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.MainScreenView
import com.bunbeauty.ideal.myapplication.helpApi.PanelBuilder
import com.bunbeauty.ideal.myapplication.helpApi.Search
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class MainScreenActivity : MvpAppCompatActivity(), View.OnClickListener, MainScreenView {

    private lateinit var search: Search

    private lateinit var categoriesBtns: Array<Button>
    private lateinit var categories: ArrayList<String>
    private lateinit var selectedTagsArray: ArrayList<String>
    private lateinit var categoryLayout: LinearLayout
    private lateinit var tagsLayout: LinearLayout
    private lateinit var innerLayout: LinearLayout
    private lateinit var progressBar: ProgressBar

    private lateinit var serviceList: ArrayList<Service>
    private lateinit var userList: ArrayList<User>
    private lateinit var recyclerView: RecyclerView
    private lateinit var serviceAdapter: ServiceAdapter
    private var isUpdated: Boolean = false
    private lateinit var category: String

    private val userId: String
        get() = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_screen)

        init()
        startLoading()
        createCategoryFeed()
        createMainScreen()
    }

    private fun init() {
        search = Search(this)
        serviceList = ArrayList()
        userList = ArrayList()
        //isUpdated = true
        categories = ArrayList(listOf(*resources.getStringArray(R.array.categories)))
        selectedTagsArray = ArrayList()
        //categoriesBtns = arrayOfNulls(categories.size)

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
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (recyclerView.computeVerticalScrollOffset() == 0 && !isUpdated)
                //check for scroll down
                {
                    serviceList.clear()
                    userList.clear()
                    progressBar.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                }
            }
        })
        minimizeTagsBtn.setOnClickListener(this)
        clearTagsBtn.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.minimizeTagsMainScreenBtn -> hideTags()

            R.id.clearTagsMainScreenBtn -> {
                startLoading()
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
        startLoading()

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

    override fun startLoading() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        serviceList.clear()
        userList.clear()
    }

    private fun categoriesClick(btn: Button) {
        // Если категория уже выбрана
        if (category == btn.text.toString()) {
            if (tagsLayout.visibility == View.VISIBLE) {
                hideTags()
            } else {
                showTags()
            }
        } else {
            startLoading()
            enableCategory(btn)
            //category = btn.getText().toString();
            createMainScreen()
        }
    }

    private fun clearCategory() {
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
        showTags()
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

    override fun onResume() {
        super.onResume()

        val panelBuilder = PanelBuilder()
        panelBuilder.buildFooter(supportFragmentManager, R.id.footerMainScreenLayout)
        panelBuilder.buildHeader(supportFragmentManager, "Главная", R.id.headerMainScreenLayout)
    }

    private fun createMainScreen() {
        //получаем id пользователя
        val userId = userId

        //получаем город юзера
        val userCity = getUserCity(userId)

        //получаем все сервисы, которые находятся в городе юзера
        getServicesInThisCity(userCity, category, selectedTagsArray)
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

    private fun getServicesInThisCity(userCity: String, category: String, selectedTagsArray: ArrayList<String>?) {

        //возвращение всех пользователей из контретного города
        val userQuery = FirebaseDatabase.getInstance().getReference(User.USERS)
                .orderByChild(User.CITY)
                .equalTo(userCity)

        userQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(usersSnapshot: DataSnapshot) {

                val commonList = search.getServicesOfUsers(usersSnapshot,
                        null, null, null,
                        category,
                        selectedTagsArray)
                for (serviceData in commonList) {
                    serviceList.add(serviceData[1] as Service)
                    userList.add(serviceData[2] as User)
                }
                serviceAdapter = ServiceAdapter(serviceList.size, serviceList, userList)
                recyclerView.adapter = serviceAdapter
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    override fun showTags() {
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

        tagsLayout.visibility = View.VISIBLE
    }

    companion object {
        // добавить, чтобы не было видно своих сервисов
        // например номер юзера, возвращаемого сервиса не должен быть равен локальному
        private val TAG = "DBInf"
    }
}
