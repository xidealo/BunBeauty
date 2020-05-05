package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.editing

import android.os.Bundle
import android.widget.*
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.MvpFacade.init
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.editing.EditProfileInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service.EditServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.enums.ButtonTask
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.IBottomPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.ITopPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.logIn.EditServicePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.logIn.EditServiceView
import javax.inject.Inject

class EditServiceActivity : MvpAppCompatActivity() , IBottomPanel,ITopPanel,
    EditServiceView{

    private lateinit var progressEditServiceBar: ProgressBar
    private lateinit var editServiceScroll: ScrollView
    private lateinit var nameEditService: TextView
    private lateinit var addressEditService: TextView
    private lateinit var costEditService: TextView
    private lateinit var descriptionEditService:TextView
    private lateinit var saveEditServiceBtn: Button

    private fun init(){
        progressEditServiceBar =findViewById(R.id.progressEditServiceBar)
        editServiceScroll =findViewById(R.id.editServiceScroll)
        nameEditService=findViewById(R.id.nameEditService)
        addressEditService=findViewById(R.id.addressEditService)
        costEditService=findViewById(R.id.costEditService)
        descriptionEditService=findViewById(R.id.descriptionEditService)
        saveEditServiceBtn=findViewById(R.id.saveEditServiceBtn)

    }

    @Inject
    lateinit  var editServiceInteractor: EditServiceInteractor

    @Inject
    lateinit  var editServicePresenter: EditServicePresenter


    private fun createPanels(){
        createBottomPanel(supportFragmentManager)
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
}



