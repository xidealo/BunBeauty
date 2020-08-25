package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.log_in

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.business.log_in.AuthorizationInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.di.component.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.clean_architecture.di.module.AppModule
import com.bunbeauty.ideal.myapplication.clean_architecture.di.module.FirebaseModule
import com.bunbeauty.ideal.myapplication.clean_architecture.di.module.InteractorModule
import com.bunbeauty.ideal.myapplication.clean_architecture.di.module.RepositoryModule
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.profile.ProfileActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.intarfaces.IAdapterSpinner
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.log_in.AuthorizationPresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.log_in.AuthorizationView
import kotlinx.android.synthetic.main.activity_authorization.*
import javax.inject.Inject

class AuthorizationActivity : MvpAppCompatActivity(), AuthorizationView, IAdapterSpinner {

    @Inject
    lateinit var authorizationInteractor: AuthorizationInteractor

    @InjectPresenter
    lateinit var authorizationPresenter: AuthorizationPresenter

    @ProvidePresenter
    internal fun provideAuthorizationPresenter(): AuthorizationPresenter {
        DaggerAppComponent
            .builder()
            .appModule(AppModule(application))
            .firebaseModule(FirebaseModule())
            .interactorModule(InteractorModule(intent))
            .repositoryModule(RepositoryModule())
            .build().inject(this)

        return AuthorizationPresenter(authorizationInteractor)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authorization)
        configViews()

        authorizationPresenter.defaultAuthorize()
    }

    private fun configViews() {
        val countryCodes = arrayListOf(*resources.getStringArray(R.array.countryCode))

        setAdapter(
            countryCodes,
            activity_authorization_sp_code,
            this
        )
        activity_authorization_sp_code.setText(countryCodes.first())
        (activity_authorization_sp_code.adapter as ArrayAdapter<String>).filter.filter("")

        activity_authorization_btn_login.setOnClickListener {
            authorizationPresenter.authorize(activity_authorization_sp_code.text.toString() + activity_authorization_et_phone.text.toString())
        }
    }

    override fun onPause() {
        super.onPause()
        enableVerifyBtn(true)
    }

    override fun hideViewsOnScreen() {
        activity_authorization_pb_loading.visibility = View.VISIBLE

        activity_authorization_til_code.visibility = View.GONE
        activity_authorization_et_phone.visibility = View.GONE
        activity_authorization_btn_login.visibility = View.GONE
    }

    override fun showViewsOnScreen() {
        activity_authorization_pb_loading.visibility = View.GONE

        activity_authorization_til_code.visibility = View.VISIBLE
        activity_authorization_et_phone.visibility = View.VISIBLE
        activity_authorization_btn_login.visibility = View.VISIBLE
    }

    override fun showPhoneError(error: String) {
        activity_authorization_et_phone.error = error
        activity_authorization_et_phone.requestFocus()
    }

    override fun enableVerifyBtn(status: Boolean) {
        activity_authorization_btn_login.isClickable = status
    }

    override fun goToVerifyPhone(phone: String) {
        val intent = Intent(this, VerifyPhoneNumberActivity::class.java)
        intent.putExtra(User.PHONE, phone)
        this.startActivity(intent)
        overridePendingTransition(0, 0)
    }

    override fun goToRegistration(phone: String) {
        val intent = Intent(this, RegistrationActivity::class.java)
        intent.putExtra(User.PHONE, phone)
        this.startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }

    override fun goToProfile(user: User) {
        val intent = Intent(this, ProfileActivity::class.java).apply {
            putExtra(User.USER, user) }

        this.startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }

    override fun hideKeyboard() {
        //WorkWithViewApi.hideKeyboard(this)
    }

    override fun disableButton() {
        activity_authorization_btn_login.isEnabled = false
    }

    override fun enableButton() {
        activity_authorization_btn_login.isEnabled = true
    }
}