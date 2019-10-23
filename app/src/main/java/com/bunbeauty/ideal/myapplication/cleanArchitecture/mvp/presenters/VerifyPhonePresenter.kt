package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.VerifyPhoneInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.VerifyCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.VerifyPhoneView
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.logIn.VerifyPhoneActivity
import com.bunbeauty.ideal.myapplication.helpApi.LoadingGuestServiceData
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.util.*
import java.util.concurrent.TimeUnit

@InjectViewState
class VerifyPhonePresenter(private val verifyPhoneInteractor: VerifyPhoneInteractor):
        MvpPresenter<VerifyPhoneView>(), VerifyCallback {

    private val TAG = "DBInf"
    private val SEND = "send"
    private val RESEND = "resend"
    private lateinit var phoneVerificationId: String
    private lateinit var activity: VerifyPhoneActivity
    private var userName: String = ""

    //List of actions ('send' or 'resend')
    var sendingActions = LinkedList<String>()
    var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    fun sendCode(verifyPhoneActivity: VerifyPhoneActivity) {
        addAction(SEND)
        activity = verifyPhoneActivity

        verifyPhoneInteractor.getMyPhoneNumber(this)
        viewState.showSendCode()
    }

    override fun callbackGetUserPhone(phone: String) {
        if (sendingActions.size > 0 && sendingActions[0].equals(SEND)) {
            sendVerificationCode(phone)
        } else {
            resendVerificationCode(phone, resendToken!!, activity)
        }

        removeFirstAction()
    }

    override fun callbackGetUserName(name: String) {
        this.userName = name
    }

    fun verify(code:String){
        if (code.trim().length >= 6) {
            verifyCode(code)
            viewState.hideViewsOnScreen()
        } else {
            viewState.showWrongCode()
        }
    }

    fun resendCode(){
        addAction(RESEND)

        verifyPhoneInteractor.getMyPhoneNumber(this)
        viewState.showSendCode()
    }

    private fun verifyCode(code: String) {
        //получаем ответ гугл
        val credential = PhoneAuthProvider.getCredential(phoneVerificationId, code)
        //заходим с айфоном и токеном
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        val fbAuth: FirebaseAuth = FirebaseAuth.getInstance()

        fbAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            //если введен верный код
            if (task.isSuccessful) {
                if (userName.isEmpty()) {
                    viewState.goToRegistration()
                } else {
                    viewState.goToProfile()
                }
            } else {
                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    viewState.callbackWrongCode()
                }
            }
        }
    }

    private fun sendVerificationCode(phoneNumber: String) {
        Log.d(TAG, "send")
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber, // Phone number to verify
                60, // Timeout duration
                TimeUnit.SECONDS, // Unit of timeout
                activity, // Activity (for callback binding)
                verificationCallbacks)
    }

    private fun resendVerificationCode(phoneNumber: String, token: PhoneAuthProvider.ForceResendingToken,
                                       verifyPhoneActivity: VerifyPhoneActivity) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber, // Phone number to verify
                60, // Timeout duration
                TimeUnit.SECONDS, // Unit of timeout
                verifyPhoneActivity, // Activity (for callback binding)
                verificationCallbacks, // OnVerificationStateChangedCallbacks
                token)  // ForceResendingToken from callbacks
    }

    // Work with list of actions
    private fun addAction(action: String) {
        sendingActions.add(action)
    }

    private fun removeFirstAction() {
        sendingActions.removeAt(0)
    }

    private val verificationCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            //вызывается, если номер подтвержден
        }

        override fun onVerificationFailed(e: FirebaseException) {
            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Log.d(TAG, "Invalid credential: " + e.getLocalizedMessage())
            } else if (e is FirebaseTooManyRequestsException) {
                // SMS quota exceeded
                Log.d(TAG, "SMS Quota exceeded.")
            }
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            //происходит, когда отослали код
            phoneVerificationId = verificationId
            resendToken = token
        }
    }
}