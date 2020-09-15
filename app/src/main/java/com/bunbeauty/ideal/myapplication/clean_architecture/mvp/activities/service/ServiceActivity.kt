package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.service

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.adapters.PhotoAdapter
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.adapters.elements.photoElement.IPhotoElement
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.api.gone
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.api.visible
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.service.ServiceInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.service.ServicePhotoInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.service.ServiceUserInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.enums.ButtonTask
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.PhotoSliderActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.SessionsActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.comments.ServiceCommentsActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.editing.EditServiceActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.fragments.PremiumFragment
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.interfaces.IProfileAvailable
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.profile.ProfileActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.base.BaseActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.ServicePresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.ServiceView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_service.*
import javax.inject.Inject

class ServiceActivity : BaseActivity(), ServiceView, IProfileAvailable, IPhotoElement {

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
        buildDagger().inject(this)
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
        activity_service_rv_photos.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        activity_service_rv_photos.adapter = photoAdapter
    }

    @SuppressLint("SetTextI18n")
    override fun showService(service: Service) {
        activity_service_tv_cost.text = service.cost.toString() + "₽"
        activity_service_tv_address.text = service.address
        activity_service_tv_description.text = service.description
        activity_service_tv_duration.text = if (service.duration.toInt() > 0) {
            (service.duration / 1).toInt().toString() +
                    if (service.duration % 1 > 0) {
                        " ч 30 мин"
                    } else {
                        " ч"
                    }
        } else {
            "30 мин"
        }

        showRating(service.rating, service.countOfRates)

        supportFragmentManager
            .beginTransaction()
            .add(R.id.activity_service_ll_premium,  PremiumFragment.newInstance(service), "premium")
            .commit()
    }

    override fun showLoading() {
        activity_service_pb_loading.visible()
        activity_service_sv_main.gone()
        activity_service_btn_schedule.gone()
    }

    override fun setTopPanelTitle(title: String) {
        setTitle(title)
    }

    override fun hideLoading() {
        activity_service_pb_loading.gone()
        activity_service_sv_main.visible()
    }

    override fun showRating(rating: Float, countOfRates: Long) {
        if (countOfRates > 0) {
            showRatingBar(rating)
            activity_service_tv_count.text = countOfRates.toString()
            activity_service_tv_rating.text = rating.toString()
        } else {
            showWithoutRating()
        }
    }

    private fun showWithoutRating() {
        activity_service_tv_empty.visible()
        activity_service_ll_rating.gone()
    }

    private fun showRatingBar(rating: Float) {
        activity_service_tv_empty.gone()
        activity_service_ll_rating.visible()
        activity_service_rb_rating.rating = rating
        activity_service_ll_rating_block.setOnClickListener {
            goToComments()
        }
    }

    override fun createOwnServiceTopPanel(service: Service) {
        initTopPanel(service.name, ButtonTask.EDIT)
    }

    override fun createAlienServiceTopPanel(user: User, service: Service) {
        initTopPanel(service.name, ButtonTask.GO_TO_PROFILE, user.photoLink)
    }

    override fun showPremium() {
        activity_service_ll_premium.visible()

    }

    override fun hidePremium() {
        activity_service_ll_premium.gone()
    }

    override fun showPhotos(photos: List<Photo>) {
        photoAdapter.setData(
            photos,
            this,
            resources.getDimensionPixelSize(R.dimen.photo_width),
            resources.getDimensionPixelSize(R.dimen.photo_height)
        )
    }

    override fun showPremiumButton() {
        activity_service_btn_premium.visible()
        activity_service_btn_premium.setOnClickListener {
            showPremium()
            activity_service_btn_premium.gone()
        }
    }

    override fun hidePremiumButton() {
        activity_service_btn_premium.gone()
    }

    override fun showScheduleButton() {
        activity_service_btn_schedule.visible()
        activity_service_btn_schedule.setOnClickListener {
            goToSessions()
        }
    }

    override fun hideScheduleButton() {
        activity_service_btn_schedule.gone()
    }

    override fun actionClick() {
        servicePresenter.iconClick()
    }

    override fun showMessage(message: String) {
        Snackbar.make(activity_service_cl_main, message, Snackbar.LENGTH_LONG)
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
                servicePresenter.updateService(data.getParcelableExtra<Service>(Service.SERVICE) as Service)
            }
        }
    }

    override fun goToEditService(service: Service) {
        val intent = Intent(this, EditServiceActivity::class.java)
        intent.putExtra(Service.SERVICE, service)
        startActivityForResult(intent, REQUEST_EDIT_SERVICE)
        overridePendingTransition(0, 0)
    }

    override fun goToProfile(user: User) {
        val intent = Intent(this, ProfileActivity::class.java).apply {
            putExtra(User.USER, user)
        }
        startActivity(intent)
        overridePendingTransition(0, 0)
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
        val intent = Intent(this, ServiceCommentsActivity::class.java).apply {
            putExtra(Service.SERVICE, servicePresenter.getGottenService())
        }
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    private fun goToSessions() {
        val intent = Intent(this, SessionsActivity::class.java).apply {
            putExtra(Service.SERVICE, servicePresenter.getGottenService())
        }
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    companion object {
        private const val REQUEST_EDIT_SERVICE = 1
    }


}