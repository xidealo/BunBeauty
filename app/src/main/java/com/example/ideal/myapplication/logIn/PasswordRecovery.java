package com.example.ideal.myapplication.logIn;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.helpApi.WorkWithViewApi;
import com.example.ideal.myapplication.test.ProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class PasswordRecovery extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "DBInf";
    private static final String USERS = "users";
    private static final String PHONE_NUMBER = "Phone number";

    private Button sendCodeBtn;
    private Button verifyButton;
    private Button resendButton;

    private EditText phoneText;
    private EditText codeText;

    private Spinner codeSpinner;

    private FirebaseAuth fbAuth;

    private String phone;
    private String phoneVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_recovery);

        sendCodeBtn = findViewById(R.id.sendCodeRecoveryBtn);
        verifyButton = findViewById(R.id.verifyRecoveryBtn);
        resendButton = findViewById(R.id.resendRecoveryBtn);

        phoneText = findViewById(R.id.phoneRecoveryInput);
        codeText = findViewById(R.id.codeRecoveryInput);

        codeSpinner = findViewById(R.id.codeRecoverySpinner);
        codeSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, CountryCodes.countryNames));

        fbAuth = FirebaseAuth.getInstance();

        sendCodeBtn.setOnClickListener(this);
        resendButton.setOnClickListener(this);
        verifyButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        WorkWithViewApi.hideKeyboard(this);

        switch (v.getId()) {
            case R.id.sendCodeRecoveryBtn:
                //получаем данные
                phone = phoneText.getText().toString().trim();
                if (phone.isEmpty() || phone.length() < 9) {
                    phoneText.setError("Valid number is required");
                    phoneText.requestFocus();
                    return;
                }

                String countryCode = CountryCodes.codes[codeSpinner.getSelectedItemPosition()];
                phone = countryCode + phone;

                sendCode();
                break;

            case R.id.verifyRecoveryBtn:
                String code = codeText.getText().toString().trim();

                if (code.isEmpty() || code.length() < 6) {

                    codeText.setError("Enter code...");
                    codeText.requestFocus();
                    return;
                }
                verifyCode(code);
                break;

            case R.id.resendRecoveryBtn:
                if (resendToken != null) {
                    resendVerificationCode(resendToken);
                }
                break;

        }
    }

    public void sendCode() {

        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference myRef = database.getReference(USERS).child(phone);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()==0) {
                    attentionThisUserDoesNotExist();
                }
                else {
                    setUpVerificationCallbacks();
                    sendingCode();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                attentionBadConnection();
            }
        });

    }

    public void verifyCode(String code) {
        //получаем ответ гугл
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(phoneVerificationId, code);
        //заходим с айфоном и токеном
        signInWithPhoneAuthCredential(credential);
    }

    private void sendingCode(){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                verificationCallbacks);
    }

    private void resendVerificationCode(PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                verificationCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }



    private void setUpVerificationCallbacks() {

        verificationCallbacks =
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                        //происходит, когда отослали код
                        phoneVerificationId = verificationId;
                        resendToken = token;

                        codeText.setVisibility(View.VISIBLE);
                        verifyButton.setVisibility(View.VISIBLE);
                        resendButton.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        String code = credential.getSmsCode();
                        if (code != null) {
                            codeText.setText(code);
                            verifyCode(code);
                        }
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
                };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        //входим
        fbAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //если введенный код совпадает с присланным кодом
                        if (task.isSuccessful()) {

                            goToCreatingNewPassword();

                        } else {
                            if (task.getException() instanceof
                                    FirebaseAuthInvalidCredentialsException) {
                                attentionThisCodeWasWrong();
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    private void goToCreatingNewPassword() {
        Intent intent = new Intent(this, CreatingNewPassword.class);
        intent.putExtra(PHONE_NUMBER, phone);
        startActivity(intent);
    }

    private void attentionThisCodeWasWrong(){
        Toast.makeText(
                this,
                "Код введен неверно",
                Toast.LENGTH_SHORT).show();
    }

    private void attentionThisUserDoesNotExist() {
        Toast.makeText(
                this,
                "Пользователь с таким номером не зарегистрирован",
                Toast.LENGTH_SHORT).show();
    }

    private void attentionBadConnection() {
        Toast.makeText(
                this,
                "Плохое соединение",
                Toast.LENGTH_SHORT).show();
    }
}
