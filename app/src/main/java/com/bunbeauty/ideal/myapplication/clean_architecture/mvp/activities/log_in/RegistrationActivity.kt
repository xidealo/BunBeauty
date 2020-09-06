package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.log_in

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.WorkWithViewApi
import com.bunbeauty.ideal.myapplication.clean_architecture.business.WorkWithStringsApi
import com.bunbeauty.ideal.myapplication.clean_architecture.business.log_in.iLogIn.IRegistrationUserInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.business.api.gone
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.profile.ProfileActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.base.BaseActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.intarfaces.IAdapterSpinner
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.log_in.RegistrationPresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.log_in.RegistrationView
import com.bunbeauty.ideal.myapplication.clean_architecture.business.api.visible
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
            cityRegistrationSpinner,
            this
        )
        (cityRegistrationSpinner.adapter as ArrayAdapter<String>).filter.filter("")

        activity_registration_btn_register.setOnClickListener {
            WorkWithViewApi.hideKeyboard(this)
            registrationPresenter.registerUser(
                WorkWithStringsApi.firstCapitalSymbol(activity_registration_et_name.text.toString().trim()),
                WorkWithStringsApi.firstCapitalSymbol(
                    activity_registration_et_surname.text.toString().trim()
                ),
                WorkWithStringsApi.firstCapitalSymbol(cityRegistrationSpinner.text.toString()),
                activity_registration_et_phone.text.toString()
            )
        }
    }

    override fun showLoading() {
        activity_registration_btn_register.gone()
        activity_registration_pb_loading.visible()
    }

    override fun hideLoading() {
        activity_registration_btn_register.visible()
        activity_registration_pb_loading.gone()
    }

    override fun fillPhoneInput(phone: String) = activity_registration_et_phone.setText(phone)

    override fun disableRegistrationButton() {
        activity_registration_btn_register.isEnabled = false
    }

    override fun enableRegistrationButton() {
        activity_registration_btn_register.isEnabled = true
    }

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
    //TODO (dont work)
    override fun showSuccessfulRegistration() {
        Snackbar.make(activity_registration_ll, "Пользователь зарегестирован", Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(this, R.color.mainBlue))
            .setActionTextColor(ContextCompat.getColor(this, R.color.white)).show()
    }

    override fun goToProfile(user: User) {
        val intent = Intent(this, ProfileActivity::class.java).apply {
            putExtra(User.USER, user)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }
}