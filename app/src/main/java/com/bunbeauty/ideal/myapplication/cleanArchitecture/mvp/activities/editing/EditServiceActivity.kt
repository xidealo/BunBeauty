package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.editing

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service.EditServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.enums.ButtonTask
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.IBottomPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.ITopPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.logIn.EditServicePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.logIn.EditServiceView
import com.google.android.material.bottomnavigation.BottomNavigationView
import javax.inject.Inject

class EditServiceActivity : MvpAppCompatActivity(), IBottomPanel, ITopPanel,
    EditServiceView {

    private lateinit var progressEditServiceBar: ProgressBar
    private lateinit var editServiceScroll: ScrollView
    private lateinit var nameEditService: TextView
    private lateinit var addressEditService: TextView
    private lateinit var costEditService: TextView
    private lateinit var descriptionEditService: TextView
    private lateinit var saveEditServiceBtn: Button

    override var bottomNavigationContext: Context = this
    override lateinit var bottomPanel: BottomNavigationView

    private fun init() {
        progressEditServiceBar = findViewById(R.id.progressEditServiceBar)
        editServiceScroll = findViewById(R.id.editServiceScroll)
        nameEditService = findViewById(R.id.nameEditService)
        addressEditService = findViewById(R.id.addressEditService)
        costEditService = findViewById(R.id.costEditService)
        descriptionEditService = findViewById(R.id.descriptionEditService)
        saveEditServiceBtn = findViewById(R.id.saveEditServiceBtn)
    }

    @Inject
    lateinit var editServiceInteractor: EditServiceInteractor

    @ProvidePresenter
    internal fun provideEditServicePresenter(): EditServicePresenter {
        DaggerAppComponent
            .builder()
            .appModule(AppModule(application, intent))
            .build().inject(this)
        return EditServicePresenter(editServiceInteractor)
    }

    private fun createPanels() {
        createTopPanel("Редактирование услуги", ButtonTask.NONE, supportFragmentManager)
    }

    /* имя, адрес, описание, цена, (категории)
    дизайн,переменная,инит,панели,пресентер,интерактор,онкклик*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_service)
        init()
        createPanels()
    }

    override fun onResume() {
        super.onResume()

        initBottomPanel()
    }
}



