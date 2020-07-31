package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.service

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.ChangeablePhotoAdapter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.PhotoAdapter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.elements.photoElement.IPhotoElement
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service.ServicePhotoInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service.ServiceServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service.ServiceUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.enums.ButtonTask
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.editing.EditServiceActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.fragments.PremiumFragment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.IBottomPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.IProfileAvailable
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.ITopPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.profile.ProfileActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.ServicePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.ServiceView
import com.bunbeauty.ideal.myapplication.createService.MyCalendar
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.activity_service.*
import javax.inject.Inject

class ServiceActivity : MvpAppCompatActivity(), View.OnClickListener, ServiceView, ITopPanel,
    IBottomPanel, IProfileAvailable, IPhotoElement {

    private lateinit var premiumFragment: PremiumFragment
    override var panelContext: Activity = this
    private lateinit var photoAdapter: PhotoAdapter

    @Inject
    lateinit var serviceServiceInteractor: ServiceServiceInteractor

    @Inject
    lateinit var serviceUserInteractor: ServiceUserInteractor

    @Inject
    lateinit var servicePhotoInteractor: ServicePhotoInteractor

    @InjectPresenter
    lateinit var servicePresenter: ServicePresenter

    @ProvidePresenter
    fun provideServicePresenter(): ServicePresenter {
        DaggerAppComponent.builder()
            .appModule(AppModule(application))
            .interactorModule(InteractorModule(intent))
            .repositoryModule(RepositoryModule())
            .firebaseModule(FirebaseModule())
            .build()
            .inject(this)

        return ServicePresenter(
            serviceServiceInteractor,
            servicePhotoInteractor,
            serviceUserInteractor
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service)

        init()
        showLoading()
        servicePresenter.createServiceScreen()
        hidePremium()
    }

    override fun onResume() {
        super.onResume()
        initBottomPanel()
    }

    private fun init() {
        premiumFragment =
            supportFragmentManager.findFragmentById(R.id.premiumBlockService) as PremiumFragment
        findViewById<MaterialButton>(R.id.scheduleServiceBtn).setOnClickListener(this)

        photosServiceRecycleView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        photoAdapter = PhotoAdapter(
            servicePresenter.getPhotosLink(), this,
            resources.getDimensionPixelSize(R.dimen.photo_width),
            resources.getDimensionPixelSize(R.dimen.photo_height)
        )
        photosServiceRecycleView.adapter = photoAdapter
    }

    override fun showService(service: Service) {
        costServiceText.setText(service.cost.toString())
        addressServiceText.setText(service.address)
        descriptionServiceText.setText(service.description)
        showRating(service.rating, service.countOfRates)
    }

    override fun showLoading() {
        loadingServiceProgressBar.visibility = View.VISIBLE
        mainViewServiceScroll.visibility = View.GONE
        scheduleServiceBtn.visibility = View.GONE
    }

    override fun hideLoading() {
        loadingServiceProgressBar.visibility = View.GONE
        mainViewServiceScroll.visibility = View.VISIBLE
        scheduleServiceBtn.visibility = View.VISIBLE
    }

    override fun showRating(rating: Float, countOfRates: Long) {
        if (countOfRates > 0) {
            showRatingBar(rating)
            countOfRatesServiceText.text = countOfRates.toString()
        } else {
            showWithoutRating()
        }
    }

    private fun showWithoutRating() {
        withoutRatingServiceText.visibility = View.VISIBLE
        ratingServiceLayout.visibility = View.GONE
    }

    private fun showRatingBar(rating: Float) {
        ratingServiceRatingBar.visibility = View.VISIBLE
        ratingServiceRatingBar.rating = rating
        ratingBlockServiceLayout.setOnClickListener(this)
    }

    override fun showPremium(service: Service) {
        premiumFragment.setPremium(service)
    }

    override fun createOwnServiceTopPanel(service: Service) {
        initTopPanel(service.name, ButtonTask.EDIT)
    }

    override fun createAlienServiceTopPanel(user: User, service: Service) {
        initTopPanel(service.name, ButtonTask.GO_TO_PROFILE, user.photoLink)
    }

    override fun hidePremium() {
        premiumFragment.hidePremium()
    }

    override fun showPhotos(photos: List<Photo>) {
        photoAdapter.notifyDataSetChanged()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.scheduleServiceBtn -> {

            }

            R.id.ratingBlockServiceLayout -> goToComments()
        }
    }

    override fun actionClick() {
        servicePresenter.iconClick()
    }

    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data == null || resultCode != RESULT_OK) {
            return
        }

        when (requestCode) {
            REQUEST_EDIT_SERVICE -> {
                showMessage("Сервис изменен")
                servicePresenter.updateService(data.getSerializableExtra(Service.SERVICE) as Service)
            }
        }
    }

    override fun goToEditService(service: Service) {
        val intent = Intent(this, EditServiceActivity::class.java)
        intent.putExtra(Service.SERVICE, service)
        startActivityForResult(intent, REQUEST_EDIT_SERVICE)
    }

    private fun goToCalendar(status: String) {
        val intent = Intent(this, MyCalendar::class.java)
        //intent.putExtra(Service.SERVICE_ID, "")
        startActivity(intent)
    }

    // go to owner profile
    override fun goToProfile(user: User) {
        val intent = Intent(this, ProfileActivity::class.java).apply {
            putExtra(User.USER, user)
        }
        startActivity(intent)
    }

    private fun goToComments() {
        /*val intent = Intent(this, Comments::class.java)
        intent.putExtra(Service.SERVICE_ID, "")
        intent.putExtra(TYPE, REVIEW_FOR_SERVICE)
        intent.putExtra(Service.USER_ID, "")
        intent.putExtra(Service.COUNT_OF_RATES, "")
        startActivity(intent)*/
    }

    companion object {
        private const val REQUEST_EDIT_SERVICE = 1
    }

    override fun deletePhoto(photo: Photo) {

    }

    override fun openPhoto(openedPhotoLinkOrUri: String) {

    }

}