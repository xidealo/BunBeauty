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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import javax.inject.Inject


class EditProfileActivity : MvpAppCompatActivity(), ITopPanel, IBottomPanel, View.OnClickListener,
    EditProfileView, IAdapterSpinner {

    private lateinit var avatarEditProfileImage: ImageView
    private lateinit var logOutEditProfileBtn: Button
    private lateinit var nameEditProfileInput: TextView
    private lateinit var surnameEditProfileInput: TextView
    private lateinit var citySpinnerEditProfileSpinner: Spinner
    private lateinit var codeEditProfileSpinner: Spinner
    private lateinit var phoneEditProfileInput: TextView
    private lateinit var codeEditProfileInput: TextView
    private lateinit var verifyCodeEditProfileBtn: Button
    private lateinit var resendCodeEditProfileBtn: Button
    private lateinit var editProfileEditProfileBtn: Button

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
        avatarEditProfileImage = findViewById(R.id.avatarEditProfileImage)
        logOutEditProfileBtn = findViewById(R.id.logOutEditProfileBtn)
        nameEditProfileInput = findViewById(R.id.nameEditProfileInput)
        surnameEditProfileInput = findViewById(R.id.surnameEditProfileInput)
        citySpinnerEditProfileSpinner = findViewById(R.id.citySpinnerEditProfileSpinner)
        codeEditProfileSpinner = findViewById(R.id.codeEditProfileSpinner)
        phoneEditProfileInput = findViewById(R.id.phoneEditProfileInput)
        codeEditProfileInput = findViewById(R.id.codeEditProfileInput)
        verifyCodeEditProfileBtn = findViewById(R.id.verifyCodeEditProfileBtn)
        resendCodeEditProfileBtn = findViewById(R.id.resendCodeEditProfileBtn)
        editProfileEditProfileBtn = findViewById(R.id.editProfileEditProfileBtn)
        setAdapter(
            arrayListOf(*resources.getStringArray(R.array.choice_cites)),
            citySpinnerEditProfileSpinner,
            this
        )
        editProfileEditProfileBtn.setOnClickListener(this)
        logOutEditProfileBtn.setOnClickListener(this)
    }

    private fun createPanels() {
        initTopPanel("Редактирование профиля", ButtonTask.LOGOUT)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.editProfileEditProfileBtn -> editProfilePresenter.saveData(
                nameEditProfileInput.text.toString().trim(),
                surnameEditProfileInput.text.toString().trim(),
                citySpinnerEditProfileSpinner.selectedItem.toString(),
                phoneEditProfileInput.text.toString().trim()
            )
            R.id.logOutEditProfileBtn -> logOut()
        }
    }

    override fun disableEditProfileEditButton() {
        editProfileEditProfileBtn.isEnabled = false
    }

    override fun enableEditProfileEditButton() {
        editProfileEditProfileBtn.isEnabled = true
    }

    override fun showNoSelectedCity() {
        Toast.makeText(this, "Выберите город", Toast.LENGTH_LONG).show()
    }

    override fun showPhoneError(error: String) {
        phoneEditProfileInput.error = error
        phoneEditProfileInput.requestFocus()
    }

    override fun showEditProfile(user: User) {
        nameEditProfileInput.text = user.name
        surnameEditProfileInput.text = user.surname
        phoneEditProfileInput.text = user.phone
        showAvatar(user.photoLink)
        setSpinnerSelection(citySpinnerEditProfileSpinner, user.city)
    }

    override fun setNameEditProfileInputError(error: String) {
        nameEditProfileInput.error = error
        nameEditProfileInput.requestFocus()
    }

    override fun setSurnameEditProfileInputError(error: String) {
        surnameEditProfileInput.error = error
        surnameEditProfileInput.requestFocus()
    }

    override fun setPhoneEditProfileInputError(error: String) {
        phoneEditProfileInput.error = error
        phoneEditProfileInput.requestFocus()
    }

    override fun showAvatar(photoLink: String) {
        val width = resources.getDimensionPixelSize(R.dimen.photo_width)
        val height = resources.getDimensionPixelSize(R.dimen.photo_height)
        Picasso.get()
            .load(photoLink)
            .resize(width, height)
            .centerCrop()
            .transform(CircularTransformation())
            .into(avatarEditProfileImage)
    }

    override fun goToProfile(user: User) {
        val intent = Intent()
        intent.putExtra(User.USER, user)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun showCode() {
        codeEditProfileInput.visibility = View.VISIBLE
    }

    override fun hideCode() {
        codeEditProfileInput.visibility = View.GONE
    }

    override fun showVerifyCode() {
        verifyCodeEditProfileBtn.visibility = View.VISIBLE
    }

    override fun hideVerifyCode() {
        verifyCodeEditProfileBtn.visibility = View.GONE
    }

    override fun showResentCode() {
        resendCodeEditProfileBtn.visibility = View.VISIBLE
    }

    override fun hideResentCode() {
        resendCodeEditProfileBtn.visibility = View.GONE
    }

    fun setSpinnerSelection(spinner: Spinner, selectedValue: String) {
        var position =
            (spinner.adapter as ArrayAdapter<String>).getPosition(selectedValue)
        if (position == -1) {
            position = 0
        }
        spinner.setSelection(position)
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