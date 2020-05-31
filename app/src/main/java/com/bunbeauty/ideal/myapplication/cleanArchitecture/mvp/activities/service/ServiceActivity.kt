package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.service

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service.ServiceInteractor
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
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_service.*
import javax.inject.Inject

class ServiceActivity : MvpAppCompatActivity(), View.OnClickListener, ServiceView,
    ITopPanel, IBottomPanel, IProfileAvailable {

    private lateinit var mainScroll: ScrollView

    private lateinit var costText: TextView
    private lateinit var descriptionText: TextView
    private lateinit var addressText: TextView
    private lateinit var ratingText: TextView
    private lateinit var countOfRatesText: TextView
    private lateinit var withoutRatingText: TextView

    private lateinit var imagesLayout: LinearLayout
    private lateinit var ratingLayout: LinearLayout
    private lateinit var ratingBar: RatingBar
    private lateinit var progressBar: ProgressBar

    private lateinit var premiumFragment: PremiumFragment

    override var panelContext: Activity = this

    @Inject
    lateinit var serviceInteractor: ServiceInteractor

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

        return ServicePresenter(serviceInteractor)
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
        mainScroll = findViewById(R.id.mainServiceScroll)
        costText = findViewById(R.id.costServiceText)
        descriptionText = findViewById(R.id.descriptionServiceText)
        addressText = findViewById(R.id.addressServiceText)
        ratingText = findViewById(R.id.ratingServiceText)
        countOfRatesText = findViewById(R.id.countOfRatesServiceText)
        withoutRatingText = findViewById(R.id.withoutRatingServiceText)

        ratingLayout = findViewById(R.id.ratingServiceLayout)
        imagesLayout = findViewById(R.id.imagesServiceLayout)
        ratingBar = findViewById(R.id.ratingServiceBar)
        progressBar = findViewById(R.id.progressServiceBar)

        premiumFragment =
            supportFragmentManager.findFragmentById(R.id.premiumBlockServiceActivity) as PremiumFragment

        findViewById<MaterialButton>(R.id.scheduleServiceBtn).setOnClickListener(this)
    }

    override fun showService(service: Service) {
        costText.text = service.cost.toString()
        addressText.text = service.address
        descriptionText.text = service.description
    }

    override fun showLoading() {
        progressBar.visibility = View.VISIBLE
        mainScroll.visibility = View.GONE
        scheduleServiceBtn.visibility = View.GONE
    }

    override fun hideLoading() {
        progressBar.visibility = View.GONE
        mainScroll.visibility = View.VISIBLE
        scheduleServiceBtn.visibility = View.VISIBLE
    }

    private fun showRating(rating: Float, countOfRates: Long) {
        if (rating > 0) {
            showRatingBar(rating)
            countOfRatesText.text = countOfRates.toString()
        } else {
            showWithoutRating()
        }
    }

    private fun showWithoutRating() {
        withoutRatingText.visibility = View.VISIBLE
        ratingBar.visibility = View.GONE
    }

    private fun showRatingBar(rating: Float) {
        ratingBar.visibility = View.VISIBLE
        ratingBar.rating = rating
        ratingLayout.setOnClickListener(this)
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
        val width = resources.getDimensionPixelSize(R.dimen.photo_width)
        val height = resources.getDimensionPixelSize(R.dimen.photo_height)

        for (photo in photos) {
            val serviceImage = ImageView(applicationContext)
            val params = LinearLayout.LayoutParams(width, height)

            params.setMargins(15, 15, 15, 15)
            serviceImage.layoutParams = params
            serviceImage.scaleType = ImageView.ScaleType.CENTER_INSIDE
            imagesLayout.addView(serviceImage)

            Picasso.get()
                .load(photo.link)
                .resize(width, height)
                .centerCrop()
                .into(serviceImage)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.scheduleServiceBtn -> {
            }

            R.id.ratingServiceLayout -> goToComments()
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
        intent.putExtra(STATUS_USER_BY_SERVICE, status)
        startActivity(intent)
    }

    // go to owner profile
    override fun goToProfile(user: User) {
        val intent = Intent(this, ProfileActivity::class.java)
        intent.putExtra(User.USER, user)
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

        private val TAG = "DBInf"

        private val TYPE = "type"

        private val STATUS_USER_BY_SERVICE = "status UserCreateService"

        private val REVIEW_FOR_SERVICE = "creation_comment for service"

        private val REQUEST_EDIT_SERVICE = 1
    }

}