package com.example.ideal.myapplication.logIn;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.helpApi.WorkWithViewApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class VerifyPhone extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "DBInf";

    private static final String PHONE_NUMBER = "Phone number";
    private Button verifyCodeBtn;
    private TextView resendCodeText;
    private EditText codeInput;
    private TextView changePhoneText;
    private TextView alertCodeText;
    private ProgressBar progressBar;

    private String myPhoneNumber;
    private String phoneVerificationId;
    //private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendToken;

    private FirebaseAuth fbAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify_phone);
        //получаем id btns
        verifyCodeBtn = findViewById(R.id.verifyVerifyBtn);
        resendCodeText = findViewById(R.id.resendVerifyText);
        alertCodeText = findViewById(R.id.alertCodeVerifyText);


        //получаем id inputs
        codeInput = findViewById(R.id.codeVerifyInput);
        changePhoneText = findViewById(R.id.changePhoneVerifyText);
        fbAuth = FirebaseAuth.getInstance();
        myPhoneNumber = getIntent().getStringExtra(PHONE_NUMBER);

        progressBar = findViewById(R.id.progressBarVerifyCode);

        sendVerificationCode();

        verifyCodeBtn.setOnClickListener(this);
        resendCodeText.setOnClickListener(this);
        changePhoneText.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        WorkWithViewApi.hideKeyboard(this);
        switch (v.getId()) {
            case R.id.verifyVerifyBtn:
                String code = codeInput.getText().toString();

                if (code.trim().length() >= 6) {
                    hideViewsOfScreen();
                    // подтверждаем код и если все хорошо, создаем юзера
                    verifyCode(code);
                }
                break;

            case R.id.resendVerifyText:
                if (resendToken != null) {
                    assertResendCode();
                    resendVerificationCode(resendToken);
                }
                break;

            case R.id.changePhoneVerifyText:
                goBackToAuthorization();
                break;

            default:
                break;
        }
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallbacks =
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        //вызывается, если номер подтвержден
                        codeInput.setText("");
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {

                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // Invalid request
                            Log.d(TAG, "Invalid credential: "
                                    + e.getLocalizedMessage());
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // SMS quota exceeded
                            Log.d(TAG, "SMS Quota exceeded.");
                        }
                    }

                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                        //происходит, когда отослали код
                        phoneVerificationId = verificationId;
                        resendToken = token;

                        codeInput.setVisibility(View.VISIBLE);
                        verifyCodeBtn.setVisibility(View.VISIBLE);
                        resendCodeText.setVisibility(View.VISIBLE);
                    }
                };


    private void sendVerificationCode(){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                myPhoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                verificationCallbacks);
    }



    private void resendVerificationCode(PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                myPhoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                verificationCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    public void verifyCode(String code) {
        //получаем ответ гугл
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(phoneVerificationId, code);
        //заходим с айфоном и токеном
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        fbAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //если введен верный код
                        if (task.isSuccessful()) {
                            MyAuthorization myAuth = new MyAuthorization(VerifyPhone.this, myPhoneNumber);
                            myAuth.authorizeUser();
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                showViewsOfScreen();
                                assertWrongCode();
                                codeInput.setError("Неправильный код");
                                codeInput.requestFocus();
                            }
                        }
                    }
                });
    }

    private void hideViewsOfScreen(){
        verifyCodeBtn.setVisibility(View.GONE);
        resendCodeText.setVisibility(View.GONE);
        codeInput.setVisibility(View.GONE);
        changePhoneText.setVisibility(View.GONE);
        alertCodeText.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }
    private void showViewsOfScreen(){
        verifyCodeBtn.setVisibility(View.VISIBLE);
        resendCodeText.setVisibility(View.VISIBLE);
        codeInput.setVisibility(View.VISIBLE);
        changePhoneText.setVisibility(View.VISIBLE);
        alertCodeText.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private void assertResendCode() {
        Toast.makeText(this, "Код был отправлен", Toast.LENGTH_SHORT).show();
    }
    private void assertWrongCode() {
        Toast.makeText(this, "Вы ввели неверный код", Toast.LENGTH_SHORT).show();
    }
    private void goBackToAuthorization() {
        super.onBackPressed();
    }
}