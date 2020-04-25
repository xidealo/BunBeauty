package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.editing

import android.os.Bundle
import android.view.View
import android.widget.*
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.EditProfileInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.enums.ButtonTask
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.IBottomPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.ITopPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.EditProfilePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.EditProfileView
import org.w3c.dom.Text
import javax.inject.Inject

class EditProfileActivity : MvpAppCompatActivity(), ITopPanel, IBottomPanel, View.OnClickListener,
    EditProfileView {

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
        setContentView(R.layout.edit_profile)
        init()
        createPanels()
        editProfilePresenter.createEditProfileScreen()
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
    }


    private fun createPanels() {
        createBottomPanel(supportFragmentManager)
        createTopPanel("Редактирование профиля", ButtonTask.NONE, supportFragmentManager)
    }


    override fun onClick(v: View) {
        when (v.id) {
        }
    }

    override fun showEditProfile(user: User) {
        nameEditProfileInput.text = user.name
        surnameEditProfileInput.text = user.name
        phoneEditProfileInput.text = user.phone


    }


    /* private fun areInputsCorrect(): Boolean {
         val name = nameInput!!.text.toString()
         if (name.isEmpty()) {
             nameInput!!.error = "Введите своё имя"
             nameInput!!.requestFocus()
             return false
         }
         if (!name.matches("[a-zA-ZА-Яа-я\\-]+")) {
             nameInput!!.error = "Допустимы только буквы и тире"
             nameInput!!.requestFocus()
             return false
         }
         val surname = surnameInput!!.text.toString()
         if (surname.isEmpty()) {
             surnameInput!!.error = "Введите свою фамилию"
             surnameInput!!.requestFocus()
             return false
         }
         if (!surname.matches("[a-zA-ZА-Яа-я\\-]+")) {
             surnameInput!!.error = "Допустимы только буквы и тире"
             surnameInput!!.requestFocus()
             return false
         }
         val city = citySpinner!!.selectedItem.toString()
         if (city == "Выбрать город") {
             assertNoSelectedCity()
             return false
         }
         return true
     }*/

    /* private fun goToLogIn() {
         FirebaseAuth.getInstance().signOut()
         ListeningManager.removeAllListeners()
         stopService(Intent(this, MyService::class.java))
         //ListeningManager.removeAllListeners();
         val intent = Intent(this, AuthorizationActivity::class.java)
         intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
         startActivity(intent)
     }*/

    /* companion object {
         private const val USERS = "users"
         private const val USER_NAME = "name"
         private const val USER_CITY = "city"
         private const val PHONE = "phone"
         private const val AVATAR = "avatar"
         private const val PHOTO_LINK = "photo link"
         private const val TOKEN = "token"
     }*/


}