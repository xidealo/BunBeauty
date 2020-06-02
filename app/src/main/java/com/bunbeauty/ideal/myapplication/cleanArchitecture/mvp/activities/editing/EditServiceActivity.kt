package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.editing

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service.EditServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.FirebaseModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.InteractorModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.enums.ButtonTask
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.IBottomPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.ITopPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.logIn.EditServicePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.logIn.EditServiceView
import kotlinx.android.synthetic.main.activity_edit_service.*
import javax.inject.Inject

class EditServiceActivity : MvpAppCompatActivity(), IBottomPanel, ITopPanel,
    EditServiceView, View.OnClickListener {

    override var panelContext: Activity = this

    private fun init() {
        saveEditServiceBtn.setOnClickListener(this)
/*
        deleteEditServiceBtn.setOnClickListener(this)
*/
    }

    @Inject
    lateinit var editServiceInteractor: EditServiceInteractor

    @InjectPresenter
    lateinit var editServicePresenter: EditServicePresenter

    @ProvidePresenter
    internal fun provideEditServicePresenter(): EditServicePresenter {
        DaggerAppComponent.builder()
            .appModule(AppModule(application))
            .firebaseModule(FirebaseModule())
            .interactorModule(InteractorModule(intent))
            .build()
            .inject(this)
        return EditServicePresenter(editServiceInteractor)
    }

    private fun createPanels() {
        initTopPanel("Редактирование услуги", ButtonTask.NONE)
    }

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

    override fun showEditService(service: Service) {
        nameEditServiceInput.append(service.name)
        addressEditServiceInput.append(service.address)
        costEditServiceInput.append(service.cost.toString())
        descriptionEditServiceInput.append(service.description)
    }

    override fun goToService(service: Service) {
        val intent = Intent()
        intent.putExtra(Service.SERVICE, service)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun goToProfile(service: Service) {
        val intent = Intent()
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun showLoading() {
        progressEditServiceBar.visibility = View.VISIBLE
        editServiceScroll.visibility = View.VISIBLE
        saveEditServiceBtn.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progressEditServiceBar.visibility = View.GONE
        editServiceScroll.visibility = View.VISIBLE
        saveEditServiceBtn.visibility = View.VISIBLE
    }

    override fun enableEditServiceBtn() {
        //TODO("Not yet implemented")
    }

    override fun setNameEditServiceInputError(error: String) {
        nameEditServiceInput.error = error
        nameEditServiceInput.requestFocus()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.saveEditServiceBtn -> editServicePresenter.save(
                nameEditServiceInput.text.toString().trim(),
                addressEditServiceInput.text.toString().trim(),
                descriptionEditServiceInput.text.toString().trim(),
                costEditServiceInput.text.toString().toLongOrNull() ?: 0
            )
            /*
            R.id.deleteEditServiceBtn -> editServicePresenter.delete()
            */
        }

    }
}