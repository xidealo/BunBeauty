package com.example.ideal.myapplication.logIn;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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
import com.example.ideal.myapplication.editing.EditProfile;
import com.example.ideal.myapplication.helpApi.WorkWithLocalStorageApi;
import com.example.ideal.myapplication.helpApi.WorkWithViewApi;
import com.example.ideal.myapplication.other.DBHelper;
import com.example.ideal.myapplication.other.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class VerifyPhone extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "DBInf";

    private static final String PREVIOUS_ACTIVITY = "previous activity";
    private static final String PHONE_NUMBER = "Phone number";

    private static final String USERS = "users";
    private static final String PHONE = "phone";

    private Button verifyCodeBtn;
    private TextView resendCodeText;
    private EditText codeInput;
    private TextView changePhoneText;
    private TextView alertCodeText;
    private ProgressBar progressBar;

    private String previousActivity;
    private String phoneNumber;
    private String phoneVerificationId;
    //private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendToken;

    private FirebaseAuth fbAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify_phone);

        verifyCodeBtn = findViewById(R.id.verifyVerifyBtn);
        resendCodeText = findViewById(R.id.resendVerifyText);
        alertCodeText = findViewById(R.id.alertCodeVerifyText);
        codeInput = findViewById(R.id.codeVerifyInput);
        changePhoneText = findViewById(R.id.changePhoneVerifyText);
        progressBar = findViewById(R.id.progressBarVerifyCode);

        fbAuth = FirebaseAuth.getInstance();
        phoneNumber = getIntent().getStringExtra(PHONE_NUMBER);
        previousActivity = getIntent().getStringExtra(PREVIOUS_ACTIVITY);

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
                    attentionResendCode();
                    resendVerificationCode(resendToken);
                }
                break;

            case R.id.changePhoneVerifyText:
                goBack();
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
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                verificationCallbacks);
    }



    private void resendVerificationCode(PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                verificationCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    public void verifyCode(String code) {
        //получаем ответ гугл
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(phoneVerificationId, code);

        if (previousActivity.equals(Authorization.class.getName())) {
            signInWithPhoneAuthCredential(credential);
        }
        if (previousActivity.equals(EditProfile.class.getName())) {
            updatePhone(credential);
        }

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        fbAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //если введен верный код
                        if (task.isSuccessful()) {
                            MyAuthorization myAuth = new MyAuthorization(VerifyPhone.this, phoneNumber);
                            myAuth.authorizeUser();
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                showViewsOfScreen();
                                attentionWrongCode();
                                codeInput.setError("Неправильный код");
                                codeInput.requestFocus();
                            }
                        }
                    }
                });
    }

    private void updatePhone(PhoneAuthCredential credential) {

        FirebaseAuth.getInstance().getCurrentUser().updatePhoneNumber(credential)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //если введенный код совпадает с присланным кодом
                        if (task.isSuccessful()) {
                            savePhone();
                            goToProfile();
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                attentionWrongCode();
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    private void savePhone() {
        String userId = WorkWithLocalStorageApi.getUserId();

        DatabaseReference reference = FirebaseDatabase
                .getInstance()
                .getReference(USERS)
                .child(userId);
        Map<String, Object> items = new HashMap<>();
        items.put(PHONE, phoneNumber);
        reference.updateChildren(items);

        updateInfoInLocalStorage(userId);
    }

    private void updateInfoInLocalStorage(String userId) {
        SQLiteDatabase database = (new DBHelper(this)).getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_PHONE_USERS, phoneNumber);

        database.update(DBHelper.TABLE_CONTACTS_USERS, contentValues,
                DBHelper.KEY_ID + " = ?",
                new String[]{userId});
    }

    private void goToProfile() {
        Intent intent = new Intent(this, Profile.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
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

    private void attentionResendCode() {
        Toast.makeText(this, "Код был отправлен", Toast.LENGTH_SHORT).show();
    }

    private void attentionWrongCode() {
        Toast.makeText(this, "Введён неверный код", Toast.LENGTH_SHORT).show();
    }

    private void goBack() {
        super.onBackPressed();
    }
}