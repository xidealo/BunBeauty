package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.log_in

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.WorkWithViewApi
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.log_in.iLogIn.IRegistrationUserInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.profile.ProfileActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.base.BaseActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.intarfaces.IAdapterSpinner
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.log_in.RegistrationPresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.log_in.RegistrationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_registration.*
import javax.inject.Inject

class RegistrationActivity : BaseActivity(), RegistrationView, IAdapterSpinner {

    @Inject
    lateinit var registrationUserInteractor: IRegistrationUserInteractor

    @InjectPresenter
    lateinit var registrationPresenter: RegistrationPresenter

    @ProvidePresenter
    internal fun provideRegistrationPresenter(): RegistrationPresenter {
        buildDagger().inject(this)
        return RegistrationPresenter(registrationUserInteractor)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        configViews()
    }

    private fun configViews() {
        activity_registration_et_phone.setText(registrationPresenter.getMyPhoneNumber())
        hideLoading()
        setAdapter(
            arrayListOf(*resources.getStringArray(R.array.cities)),
            activity_registration_sp_city,
            this
        )
        (activity_registration_sp_city.adapter as ArrayAdapter<String>).filter.filter("")

        activity_registration_pbtn_register.setOnClickListener {
            WorkWithViewApi.hideKeyboard(this)
            registrationPresenter.registerUser(
                activity_registration_et_name.text.toString().trim(),
                activity_registration_et_surname.text.toString().trim(),
                activity_registration_sp_city.text.toString(),
                activity_registration_et_phone.text.toString()
            )
        }
    }

    override fun showLoading() {
        activity_registration_pbtn_register.showLoading()
    }

    override fun hideLoading() {
        activity_registration_pbtn_register.hideLoading()
    }

    override fun fillPhoneInput(phone: String) = activity_registration_et_phone.setText(phone)

    override fun setNameInputError(error: String) {
        activity_registration_et_name.error = error
        activity_registration_et_name.requestFocus()
    }

    override fun setSurnameInputError(error: String) {
        activity_registration_et_surname.error = error
        activity_registration_et_surname.requestFocus()
    }

    override fun showNoSelectedCity() {
        Snackbar.make(activity_registration_ll, "Выберите город", Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(this, R.color.red))
            .setActionTextColor(ContextCompat.getColor(this, R.color.white)).show()
    }

    override fun goToProfile(user: User) {
        val intent = Intent(this, ProfileActivity::class.java).apply {
            putExtra(User.USER, user)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }
}