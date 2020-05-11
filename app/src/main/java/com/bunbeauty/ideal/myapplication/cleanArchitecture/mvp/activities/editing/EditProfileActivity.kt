package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.editing

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.editing.EditProfileInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.enums.ButtonTask
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.IBottomPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.ITopPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.logIn.AuthorizationActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.intarfaces.IAdapterSpinner
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.EditProfilePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.EditProfileView
import com.bunbeauty.ideal.myapplication.helpApi.CircularTransformation
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import javax.inject.Inject


class EditProfileActivity : MvpAppCompatActivity(), ITopPanel, IBottomPanel, View.OnClickListener,
    EditProfileView, IAdapterSpinner {

    private lateinit var avatarImage: ImageView
    private lateinit var nameInput: TextInputEditText
    private lateinit var surnameInput: TextInputEditText
    private lateinit var citySpinner: AutoCompleteTextView
    private lateinit var codeSpinner: AutoCompleteTextView
    private lateinit var phoneInput: TextInputEditText
    private lateinit var codeLayout: TextInputLayout
    private lateinit var codeInput: TextView
    private lateinit var verifyCodeBtn: Button
    private lateinit var resendCodeBtn: Button
    private lateinit var saveChangesBtn: Button

    override var panelContext: Context = this
    override lateinit var bottomPanel: BottomNavigationView
    override lateinit var topPanel: MaterialToolbar

    @InjectPresenter
    lateinit var editProfilePresenter: EditProfilePresenter

    @Inject
    lateinit var editProfileInteractor: EditProfileInteractor

    @ProvidePresenter
    internal fun provideEditProfilePresenter(): EditProfilePresenter {
        DaggerAppComponent
            .builder()
            .appModule(AppModule(application, intent))
            .build().inject(this)
        return EditProfilePresenter(editProfileInteractor)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        init()
        createPanels()
        editProfilePresenter.createEditProfileScreen()
        hideCode()
        hideResentCode()
        hideVerifyCode()
    }

    override fun onResume() {
        super.onResume()

        initBottomPanel()
    }

    private fun init() {
        avatarImage = findViewById(R.id.avatarEditProfileImage)
        nameInput = findViewById(R.id.nameEditProfileInput)
        surnameInput = findViewById(R.id.surnameEditProfileInput)
        citySpinner = findViewById(R.id.cityEditProfileSpinner)
        codeSpinner = findViewById(R.id.codeEditProfileSpinner)
        phoneInput = findViewById(R.id.phoneEditProfileInput)
        codeInput = findViewById(R.id.codeEditProfileInput)
        codeLayout = findViewById(R.id.codeEditProfileLayout)
        verifyCodeBtn = findViewById(R.id.verifyCodeEditProfileBtn)
        resendCodeBtn = findViewById(R.id.resendCodeEditProfileBtn)
        saveChangesBtn = findViewById(R.id.saveChangesEditProfileBtn)
        setAdapter(
            arrayListOf(*resources.getStringArray(R.array.choice_cites)),
            citySpinner,
            this
        )
        setAdapter(
            arrayListOf(*resources.getStringArray(R.array.countryCode)),
            codeSpinner,
            this
        )
        saveChangesBtn.setOnClickListener(this)
    }

    private fun createPanels() {
        initTopPanel("Редактирование профиля", ButtonTask.LOGOUT)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.saveChangesEditProfileBtn -> {
                val phoneNumber = codeSpinner.text.toString() + phoneInput.text.toString().trim()

                editProfilePresenter.saveData(
                    nameInput.text.toString().trim(),
                    surnameInput.text.toString().trim(),
                    citySpinner.text.toString(),
                    phoneNumber
                )
            }
        }
    }

    override fun disableEditProfileEditButton() {
        saveChangesBtn.isEnabled = false
    }

    override fun enableEditProfileEditButton() {
        saveChangesBtn.isEnabled = true
    }

    override fun showNoSelectedCity() {
        Toast.makeText(this, "Выберите город", Toast.LENGTH_LONG).show()
    }

    override fun showPhoneError(error: String) {
        phoneInput.error = error
        phoneInput.requestFocus()
    }

    override fun showEditProfile(user: User) {
        nameInput.setText(user.name)
        surnameInput.setText(user.surname)
        phoneInput.setText(user.phone.substring(2))
        showAvatar(user.photoLink)

        citySpinner.setText(user.city)
        (citySpinner.adapter as ArrayAdapter<String>).filter.filter("")
        codeSpinner.setText(user.phone.substring(0, 2))
        (codeSpinner.adapter as ArrayAdapter<String>).filter.filter("")
    }

    override fun setNameEditProfileInputError(error: String) {
        nameInput.error = error
        nameInput.requestFocus()
    }

    override fun setSurnameEditProfileInputError(error: String) {
        surnameInput.error = error
        surnameInput.requestFocus()
    }

    override fun setPhoneEditProfileInputError(error: String) {
        phoneInput.error = error
        phoneInput.requestFocus()
    }

    override fun showAvatar(photoLink: String) {
        val width = resources.getDimensionPixelSize(R.dimen.photo_width)
        val height = resources.getDimensionPixelSize(R.dimen.photo_height)
        Picasso.get()
            .load(photoLink)
            .resize(width, height)
            .centerCrop()
            .transform(CircularTransformation())
            .into(avatarImage)
    }

    override fun goToProfile(user: User) {
        val intent = Intent()
        intent.putExtra(User.USER, user)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun showCode() {
        codeLayout.visibility = View.VISIBLE
    }

    override fun hideCode() {
        codeLayout.visibility = View.GONE
    }

    override fun showVerifyCode() {
        verifyCodeBtn.visibility = View.VISIBLE
    }

    override fun hideVerifyCode() {
        verifyCodeBtn.visibility = View.GONE
    }

    override fun showResentCode() {
        resendCodeBtn.visibility = View.VISIBLE
    }

    override fun hideResentCode() {
        resendCodeBtn.visibility = View.GONE
    }

    private fun setSpinnerSelection(spinner: AutoCompleteTextView, selectedValue: String) {
        var position = (spinner.adapter as ArrayAdapter<String>).getPosition(selectedValue)
        if (position == -1) {
            position = 0
        }
        spinner.listSelection = position
    }

    fun logOut() {
        val tokenRef = FirebaseDatabase
            .getInstance()
            .getReference(User.USERS)
            .child(User.getMyId())
            .child(User.TOKEN)
        tokenRef.setValue("-")

        val intent = Intent(this, AuthorizationActivity::class.java)
        startActivity(intent)
        overridePendingTransition(0, 0)
        FirebaseAuth.getInstance().signOut()

        finish()
    }
}