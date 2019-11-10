package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.searchService

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.bunbeauty.ideal.myapplication.adapters.ServiceAdapter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.DBHelper
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.SearchServiceView
import com.bunbeauty.ideal.myapplication.helpApi.PanelBuilder
import com.bunbeauty.ideal.myapplication.helpApi.Search
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*

class SearchServiceActivity : MvpAppCompatActivity(), View.OnClickListener, SearchServiceView{

    private var city = NOT_CHOSEN
    private var searchBy = NAME_OF_SERVICE

    private lateinit var searchLineInput: EditText

    private lateinit var recyclerView: RecyclerView
    private lateinit var serviceAdapter: ServiceAdapter

    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_service)

        showServicesInHomeTown()
        buildPanels()
    }

    fun init(){

        //создаём выпадающее меню на основе массива городов
        //Выпадающее меню
        val citySpinner = findViewById<Spinner>(R.id.citySearchServiceSpinner)
        citySpinner.prompt = NOT_CHOSEN

        //создаём выпадающее меню "Поиск по"
        val searchBySpinner = findViewById<Spinner>(R.id.searchBySearchServiceSpinner)
        searchBySpinner.prompt = NAME_OF_SERVICE

        searchLineInput = findViewById(R.id.searchLineSearchServiceInput)
        progressBar = findViewById(R.id.progressBarSearchService)
        recyclerView = findViewById(R.id.resultsSearchServiceRecycleView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val findBtn = findViewById<TextView>(R.id.findServiceSearchServiceText)
        findBtn.setOnClickListener(this)
        //отслеживаем смену городов в выпадающем меню
        citySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, itemSelected: View, selectedItemPosition: Int, selectedId: Long) {
                val cityText = itemSelected as TextView
                city = cityText.text.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        searchBySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, itemSelected: View, selectedItemPosition: Int, selectedId: Long) {
                val searchByText = itemSelected as TextView
                searchBy = searchByText.text.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

    }
    override fun onClick(v: View) {
        when (v.id) {
            R.id.findServiceSearchServiceText -> if (searchLineInput.text.toString().toLowerCase() != "") {
                showLoading()
                clearArrays()
                search()
            } else {
                showLoading()
                clearArrays()
                showServicesInHomeTown()
            }
            else -> {
            }
        }
    }

    override fun showLoading() {
        recyclerView.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
    }

    private fun showServicesInHomeTown() {
        //получаем id пользователя

        //получаем город юзера
        val userCity = getUserCity(userId)

        //получаем все сервисы, которые находятся в городе юзера
        getServicesInThisCity(userCity)
    }

    //Получает город пользователя
    private fun getUserCity(userId: String): String {
        val database = dbHelper.readableDatabase

        // Получить город юзера
        // Таблица Users
        // с фиксированным userId
        val sqlQuery = ("SELECT " + DBHelper.KEY_CITY_USERS
                + " FROM " + DBHelper.TABLE_CONTACTS_USERS
                + " WHERE " + DBHelper.KEY_ID + " = ?")

        val cursor = database.rawQuery(sqlQuery, arrayOf(userId))

        val indexCity = cursor.getColumnIndex(DBHelper.KEY_CITY_USERS)
        // дефолтное значение
        var city = "dubna"

        if (cursor.moveToFirst()) {
            city = cursor.getString(indexCity)
        }
        cursor.close()
        return city
    }

    private fun getServicesInThisCity(userCity: String) {
        val search = Search(this)

        val userQuery = FirebaseDatabase.getInstance().getReference(User.USERS)
                .orderByChild(User.CITY)
                .equalTo(userCity)

        userQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(usersSnapshot: DataSnapshot) {
                val commonList = search.getServicesOfUsers(usersSnapshot,
                        null, null, null, null, null)
                for (serviceData in commonList) {
                    serviceList.add(serviceData[1] as Service)
                    userList.add(serviceData[2] as User)
                }
                //serviceAdapter = new ServiceAdapter(serviceList.size(),serviceList,userList);
                recyclerView.adapter = serviceAdapter
                hideLoading()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                attentionBadConnection()
            }
        })
    }

    private fun search() {
        when (searchBy) {
            "название сервиса" -> searchByNameService()
            "имя и фамилия" -> searchByWorkerName()
        }
    }

    private fun searchByNameService() {
        val enteredText = searchLineInput.text.toString().toLowerCase()
        val search = Search(this)

        var usersQuery: Query = FirebaseDatabase.getInstance().getReference(User.USERS)
        if (city != NOT_CHOSEN) {
            usersQuery = usersQuery.orderByChild(User.CITY).equalTo(city)
        }

        usersQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(usersSnapshot: DataSnapshot) {
                if (usersSnapshot.value == null) {
                    attentionNothingFound()
                    return
                }

                val commonList = search.getServicesOfUsers(usersSnapshot,
                        enteredText, null, null, null, null)
                for (serviceData in commonList) {
                    serviceList.add(serviceData[1] as Service)
                    userList.add(serviceData[2] as User)
                }
                if (commonList.isEmpty()) {
                    attentionNothingFound()
                } else {
                    //serviceAdapter = new ServiceAdapter(serviceList.size(),serviceList,userList);
                    recyclerView.adapter = serviceAdapter
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                attentionBadConnection()
            }
        })
    }

    private fun searchByWorkerName() {
        val enteredText = searchLineInput.text.toString().toLowerCase()
        val search = Search(this)

        val userQuery = FirebaseDatabase.getInstance().getReference(User.USERS)
                .orderByChild(User.NAME)
                .equalTo(enteredText)
        userQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(usersSnapshot: DataSnapshot) {

                if (usersSnapshot.value == null) {
                    attentionNothingFound()
                    return
                }

                val commonList = search.getServicesOfUsers(usersSnapshot, null,
                        enteredText,
                        city, null, null)
                for (serviceData in commonList) {
                    serviceList.add(serviceData[1] as Service)
                    userList.add(serviceData[2] as User)
                }
                if (serviceList.isEmpty()) {
                    attentionNothingFound()
                } else {
                    //serviceAdapter = new ServiceAdapter(serviceList.size(),serviceList,userList);
                    recyclerView.adapter = serviceAdapter
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                attentionBadConnection()
            }
        })
    }

    override fun attentionNothingFound() {
        Toast.makeText(this, "Ничего не найдено", Toast.LENGTH_SHORT).show()
    }

    override fun attentionBadConnection() {
        Toast.makeText(this, "Плохое соединение", Toast.LENGTH_SHORT).show()
    }

    override fun buildPanels(){
        val panelBuilder = PanelBuilder()
        panelBuilder.buildFooter(supportFragmentManager, R.id.footerSearchServiceLayout)
    }

    companion object {
        // сначала идут константы
        private val TAG = "DBInf"

        const val NOT_CHOSEN = "не выбран"
        const val NAME_OF_SERVICE = "название сервиса"
    }
}
