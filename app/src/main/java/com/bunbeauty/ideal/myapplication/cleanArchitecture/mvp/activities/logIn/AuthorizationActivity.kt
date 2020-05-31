package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.logIn

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.AuthorizationInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.profile.ProfileActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.intarfaces.IAdapterSpinner
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.logIn.AuthorizationPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.logIn.AuthorizationView
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
            codeAuthorizationSpinner,
            this
        )
        codeAuthorizationSpinner.setText(countryCodes.first())
        (codeAuthorizationSpinner.adapter as ArrayAdapter<String>).filter.filter("")

        verifyAuthorizationBtn.setOnClickListener {
            authorizationPresenter.authorize(codeAuthorizationSpinner.text.toString() + phoneAuthorizationInput.text.toString())
        }
    }

    override fun onPause() {
        super.onPause()
        enableVerifyBtn(true)
    }

    override fun hideViewsOnScreen() {
        loadingAuthorizationProgressBar.visibility = View.VISIBLE

        codeAuthorizationInputLayout.visibility = View.GONE
        phoneAuthorizationInput.visibility = View.GONE
        verifyAuthorizationBtn.visibility = View.GONE
    }

    override fun showViewsOnScreen() {
        loadingAuthorizationProgressBar.visibility = View.GONE

        codeAuthorizationInputLayout.visibility = View.VISIBLE
        phoneAuthorizationInput.visibility = View.VISIBLE
        verifyAuthorizationBtn.visibility = View.VISIBLE
    }

    override fun showPhoneError(error: String) {
        phoneAuthorizationInput.error = error
        phoneAuthorizationInput.requestFocus()
    }

    override fun enableVerifyBtn(status: Boolean) {
        verifyAuthorizationBtn.isClickable = status
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
        verifyAuthorizationBtn.isEnabled = false
    }

    override fun enableButton() {
        verifyAuthorizationBtn.isEnabled = true
    }
}