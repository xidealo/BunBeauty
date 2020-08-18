package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.service

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.adapters.PhotoAdapter
import com.bunbeauty.ideal.myapplication.clean_architecture.adapters.elements.photoElement.IPhotoElement
import com.bunbeauty.ideal.myapplication.clean_architecture.business.service.ServiceInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.business.service.ServicePhotoInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.business.service.ServiceUserInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.di.*
import com.bunbeauty.ideal.myapplication.clean_architecture.enums.ButtonTask
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.PhotoSliderActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.SessionsActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.editing.EditServiceActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.fragments.PremiumFragment
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.interfaces.IBottomPanel
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.interfaces.IProfileAvailable
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.interfaces.ITopPanel
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.profile.ProfileActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.ServicePresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.ServiceView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_service.*
import javax.inject.Inject

class ServiceActivity : MvpAppCompatActivity(), ServiceView, ITopPanel, IBottomPanel,
    IProfileAvailable, IPhotoElement {

    override var panelContext: Activity = this

    private lateinit var premiumFragment: PremiumFragment

    @Inject
    lateinit var photoAdapter: PhotoAdapter

    @Inject
    lateinit var serviceInteractor: ServiceInteractor

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

        return ServicePresenter(serviceInteractor, servicePhotoInteractor, serviceUserInteractor)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service)

        init()
        showLoading()
        servicePresenter.getService()
        hidePremium()
    }

    override fun onResume() {
        super.onResume()

        initBottomPanel()
    }

    private fun init() {
        premiumFragment =
            supportFragmentManager.findFragmentById(R.id.premiumBlockService) as PremiumFragment

        scheduleServiceBtn.setOnClickListener {
            goToSessions()
        }

        photosServiceRecycleView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        photosServiceRecycleView.adapter = photoAdapter
    }

    override fun showService(service: Service) {
        costServiceText.text = service.cost.toString() + "₽"
        addressServiceText.text = service.address
        descriptionServiceText.text = service.description
        showRating(service.rating, service.countOfRates)
    }

    override fun showLoading() {
        loadingServiceProgressBar.visibility = View.VISIBLE
        mainViewServiceScroll.visibility = View.GONE
        scheduleServiceBtn.visibility = View.GONE
    }

    override fun setTopPanelTitle(title: String) {
        setTitle(title)
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
        ratingBlockServiceLayout.setOnClickListener {
            goToComments()
        }
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
        photoAdapter.setData(
            photos,
            this,
            resources.getDimensionPixelSize(R.dimen.photo_width),
            resources.getDimensionPixelSize(R.dimen.photo_height)
        )
    }

    override fun actionClick() {
        servicePresenter.iconClick()
    }

    override fun showMessage(message: String) {
        Snackbar.make(serviceMainLayout, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(this, R.color.mainBlue))
            .setActionTextColor(ContextCompat.getColor(this, R.color.white)).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data == null || resultCode != RESULT_OK) {
            return
        }

        when (requestCode) {
            REQUEST_EDIT_SERVICE -> {
                showMessage("Услуга изменена")
                servicePresenter.updateService(data.getSerializableExtra(Service.SERVICE) as Service)
            }
        }
    }

    override fun goToEditService(service: Service) {
        val intent = Intent(this, EditServiceActivity::class.java)
        intent.putExtra(Service.SERVICE, service)
        startActivityForResult(intent, REQUEST_EDIT_SERVICE)
    }

    override fun goToProfile(user: User) {
        val intent = Intent(this, ProfileActivity::class.java).apply {
            putExtra(User.USER, user)
        }
        startActivity(intent)
    }

    override fun openPhoto(openedPhotoLinkOrUri: String) {
        val intent = Intent(this, PhotoSliderActivity::class.java).apply {
            putParcelableArrayListExtra(Photo.PHOTO, ArrayList(servicePresenter.getPhotosLink()))
            putExtra(Photo.LINK, openedPhotoLinkOrUri)
        }
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    private fun goToComments() {
        /*val intent = Intent(this, Comments::class.java)
        intent.putExtra(Service.SERVICE_ID, "")
        intent.putExtra(TYPE, REVIEW_FOR_SERVICE)
        intent.putExtra(Service.USER_ID, "")
        intent.putExtra(Service.COUNT_OF_RATES, "")
        startActivity(intent)*/
    }

    private fun goToSessions() {
        val intent = Intent(this, SessionsActivity::class.java).apply {
            this.putExtra(Service.SERVICE, servicePresenter.getGottenService())
        }
        startActivity(intent)
    }

    companion object {
        private const val REQUEST_EDIT_SERVICE = 1
    }


}