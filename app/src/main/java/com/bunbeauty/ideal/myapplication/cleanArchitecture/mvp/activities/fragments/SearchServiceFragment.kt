package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.fragments.SearchServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.ITopMainScreenPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.searchService.MainScreenActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.fragments.SearchServicePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.MainScreenView
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.fragments.SearchServiceFragmentView
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithStringsApi
import java.util.*
import javax.inject.Inject

class SearchServiceFragment: MvpAppCompatFragment(), View.OnClickListener, SearchServiceFragmentView {

    private var city = NOT_CHOSEN
    private var searchBy = NAME_OF_SERVICE
    private lateinit var searchLineInput: EditText
    private lateinit var backText: TextView
    private lateinit var citySpinner: Spinner

    @InjectPresenter
    lateinit var searchServicePresenter: SearchServicePresenter
    @Inject
    lateinit var searchServiceInteractor: SearchServiceInteractor

    @ProvidePresenter
    internal fun provideAddingServicePresenter(): SearchServicePresenter {
        DaggerAppComponent
                .builder()
                .appModule(AppModule(activity!!.application, activity!!.intent))
                .build().inject(this)

        return SearchServicePresenter(searchServiceInteractor)
    }

    //fragment просто вызывает методы поиска мс, больше ничего не делает?
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.search_service, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init(view)
        searchServicePresenter.setMyCity(ArrayList(listOf(*resources.getStringArray(R.array.cities))))
        when (context!!.javaClass.name) {
            MainScreenActivity::class.java.name -> {
                setBack()
            }
        }
    }

    private fun setBack() {
        backText.setOnClickListener {
            (activity as MainScreenView).hideSearchPanel()
            (activity as ITopMainScreenPanel).showTopPanel()
            (activity as MainScreenView).showCategory()
            (activity as MainScreenView).createMainScreen()
        }
    }

    private fun init(view: View) {

        //создаём выпадающее меню на основе массива городов
        //Выпадающее меню
        citySpinner = view.findViewById(R.id.citySearchServiceSpinner)

        //создаём выпадающее меню "Поиск по"
        val searchBySpinner = view.findViewById<Spinner>(R.id.searchBySearchServiceSpinner)
        searchBySpinner.prompt = NAME_OF_SERVICE

        searchLineInput = view.findViewById(R.id.searchLineSearchServiceInput)
        backText = view.findViewById(R.id.backSearchFragmentText)

        val findBtn = view.findViewById<TextView>(R.id.findServiceSearchServiceText)
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
            R.id.findServiceSearchServiceText ->
                if (searchLineInput.text.toString().trim().isNotEmpty()) {
                //обпращаемся к презентору, метод который будет осуществлять поиск
                search(searchLineInput.text.toString())
            }else{
                    (activity as MainScreenView).createMainScreen()
                }
        }
    }

    private fun search(data:String) {
        when (searchBy) {
            NAME_OF_SERVICE ->  (activity as MainScreenView).showMainScreenByServiceName(city, WorkWithStringsApi.firstCapitalSymbol(data))
            NICKNAME ->  (activity as MainScreenView).showMainScreenByUserName(city, WorkWithStringsApi.doubleCapitalSymbols(data))
        }
    }

    override fun attentionNothingFound() {
        Toast.makeText(context, "Ничего не найдено", Toast.LENGTH_SHORT).show()
    }

    override fun attentionBadConnection() {
        Toast.makeText(context, "Плохое соединение", Toast.LENGTH_SHORT).show()
    }

    override fun showMyCity(position: Int) {
        citySpinner.setSelection(position)
    }

    companion object {
        // сначала идут константы
        private val TAG = "DBInf"
        const val NOT_CHOSEN = "не выбран"
        const val NAME_OF_SERVICE = "название сервиса"
        const val NICKNAME = "имя и фамилия"
    }
}
