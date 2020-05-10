package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.editing



import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service.EditServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.enums.ButtonTask
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.IBottomPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.ITopPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.ServicePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.logIn.EditServicePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.logIn.EditServiceView
import kotlinx.android.synthetic.main.activity_edit_service.*
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import javax.inject.Inject

class EditServiceActivity : MvpAppCompatActivity(), IBottomPanel, ITopPanel,
    EditServiceView,View.OnClickListener {

    private lateinit var progressEditServiceBar: ProgressBar
    private lateinit var editServiceScroll: ScrollView
    private lateinit var nameEditService: TextView
    private lateinit var addressEditService: TextView
    private lateinit var costEditService: TextView
    private lateinit var descriptionEditService: TextView
    private lateinit var saveEditServiceBtn: Button

    override var panelContext: Context = this
    override lateinit var bottomPanel: BottomNavigationView
    override lateinit var topPanel: MaterialToolbar

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

    @InjectPresenter
    private lateinit var editServicePresenter: EditServicePresenter

    //ДОБАВИЛ!
    @ProvidePresenter
    internal fun provideEditServicePresenter(): EditServicePresenter {
        DaggerAppComponent
            .builder()
            .appModule(AppModule(application, intent))
            .build().inject(this)
        return EditServicePresenter(editServiceInteractor)
    }

    private fun createPanels() {
        initTopPanel("Редактирование услуги", ButtonTask.NONE)
    }

    /* имя, адрес, описание, цена, (категории)
    дизайн,переменная,инит,панели,пресентер,интерактор,онкклик*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_service)
        init()
        showLoading()
        editServicePresenter.createEditServiceScreen()
        createPanels()
    }

    override fun onResume() {
        super.onResume()

        initBottomPanel()
    }

    override  fun showEditService(service:Service){
        nameEditService.text= service.name
        addressEditService.text= service.address
        costEditService.text = service.cost.toString()
        descriptionEditService.text= service.description
    }

    override fun goToService(service: Service) {
        val intent = Intent()
        intent.putExtra(Service.SERVICE, service)
        setResult(RESULT_OK, intent)
        finish()

    }


    override fun showLoading (){
        progressEditServiceBar.visibility= View.VISIBLE
        editServiceScroll.visibility = View.VISIBLE
        saveEditServiceBtn.visibility = View.VISIBLE
    }

    override fun hideLoading(){
        progressEditServiceBar.visibility= View.GONE
        editServiceScroll.visibility = View.VISIBLE
        saveEditServiceBtn.visibility = View.VISIBLE
    }

    override fun enableEditServiceBtn() {

    }

    override fun setNameEditServiceInputError(error: String) {
        nameEditService.error = error
        nameEditService.requestFocus()
    }


    override fun onClick(v: View) {
        when(v.id){
            R.id.saveEditServiceBtn->editServicePresenter.save(
                nameEditService.text.toString().trim(),
                addressEditService.text.toString().trim(),
                descriptionEditService.text.toString().trim(),
                costEditService.text.toString().toLongOrNull() ?:0
            )
        }

    }

}



