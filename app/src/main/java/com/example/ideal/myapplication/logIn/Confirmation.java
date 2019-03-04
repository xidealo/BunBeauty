package com.example.ideal.myapplication.logIn;

import android.content.Intent;
import android.content.SharedPreferences;
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

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Confirmation extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "DBInf";

    private static final String PASS = "password";
    private static final String FILE_NAME = "Info";
    private static final String PHONE_NUMBER = "Phone number";

    private static final String USERS = "users";

    private Button registrationButton;
    private Button verifyButton;
    private Button resendButton;

    private EditText phoneInput;
    private EditText passwordInput;
    private EditText repeatpasswordInput;
    private EditText codeText;

    private Spinner codeSpinner;

    SharedPreferences sPref;

    private String phone;
    private String phoneVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            verificationCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendToken;

    private FirebaseAuth fbAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmation);
        //получаем id btns
        registrationButton = findViewById(R.id.registrationConfirmationBtn);
        verifyButton = findViewById(R.id.verifyConfirmationBtn);
        resendButton = findViewById(R.id.resendConfirmationBtn);

        //получаем id inputs
        phoneInput = findViewById(R.id.phoneConfirmationInput);
        passwordInput = findViewById(R.id.passConfirmationInput);
        repeatpasswordInput = findViewById(R.id.repeatPassConfirmationInput);
        codeText = findViewById(R.id.codeConfirmationInput);

        codeSpinner = findViewById(R.id.codeConfirmationSpinner);
        codeSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, CountryCodes.countryNames));

        fbAuth = FirebaseAuth.getInstance();

        registrationButton.setOnClickListener(this);
        resendButton.setOnClickListener(this);
        verifyButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        WorkWithViewApi.hideKeyboard(this);

        switch (v.getId()) {
            case R.id.registrationConfirmationBtn:
                //получаем данные
                phone = phoneInput.getText().toString().trim();
                String password = passwordInput.getText().toString();
                String repeatPassword = repeatpasswordInput.getText().toString();

                //проверка на заполенность полей
                if(isFullInputs()){
                    if (phone.isEmpty() || phone.length() < 9) {
                        phoneInput.setError("Valid number is required");
                        phoneInput.requestFocus();
                        return;
                    }

                    String countryCode = CountryCodes.codes[codeSpinner.getSelectedItemPosition()];
                    phone = countryCode + phone;
                    // проверка на надежность пароля
                    if(isStrongPassword(password)) {
                        // проверка на совпадение паролей
                        if (password.equals(repeatPassword)) {
                            //отправляем код, если пользователь еще не подтвержден
                            sendCode(phone);
                        }
                        else {
                            Toast.makeText(
                                    this,
                                    "Пароли не совпадают",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(
                                this,
                                "Пароль должен содержать буквы и цифры и быть не менее 6 символов.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case R.id.verifyConfirmationBtn:
                String code = codeText.getText().toString();

                if(!code.trim().equals("")) {
                    // подтверждаем код и если все хорошо, создаем юзера
                    verifyCode(code);
                }
                break;

            case R.id.resendConfirmationBtn:
                if(resendToken != null) {
                    resendVerificationCode(resendToken);
                    Log.d(TAG, "resendToken: " + resendToken.toString());
                }
                break;

            default:
                break;
        }
    }

    private void createNewUser(String phoneNumber, String pass) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users/"+phoneNumber);
        Map<String,Object> items = new HashMap<>();
        items.put(PASS, pass);
        myRef.updateChildren(items);

        goToRegistration(phoneNumber);
    }

    public void sendCode(final String phoneNumber) {

        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference myRef = database.getReference(USERS).child(phoneNumber);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()==0) {
                    setUpVerificationCallbacks();
                    phoneInput.setEnabled(false);
                    passwordInput.setVisibility(View.GONE);
                    repeatpasswordInput.setVisibility(View.GONE);
                    registrationButton.setVisibility(View.GONE);
                    sendingCode(phoneNumber);
                }
                else {
                    attentionThisUserAlreadyReg();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                attentionBadConnection();
            }
        });

    }

    private void sendingCode(String phoneNumber){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
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
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        //вызывается, если номер подтвержден
                        codeText.setText("");
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


                        codeText.setVisibility(View.VISIBLE);
                        verifyButton.setVisibility(View.VISIBLE);
                        resendButton.setVisibility(View.VISIBLE);
                    }
                };
    }

    public void verifyCode(String code) {
        //получаем ответ гугл
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(phoneVerificationId, code);
        //заходим с айфоном и токеном
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        //входим
        fbAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //если введенный код совпадает с присланным кодом
                        if (task.isSuccessful()) {
                            String password = passwordInput.getText().toString();
                            password = encryptThisStringSHA512(password);
                            //создаем пользователя
                            createNewUser(phone, password);

                            //сохраняем его в лок данные
                            saveIdAndPass(phone, password);

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

    private static String encryptThisStringSHA512(String input)
    {
        try {
            // getInstance() method is called with algorithm SHA-512
            MessageDigest md = MessageDigest.getInstance("SHA-512");

            // digest() method is called
            // to calculate message digest of the input string
            // returned as array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);

            // Add preceding 0s to make it 32 bit
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

            // return the HashText
            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    protected Boolean isFullInputs(){
        if(phoneInput.getText().toString().isEmpty()) return false;
        if(passwordInput.getText().toString().isEmpty()) return false;
        if(repeatpasswordInput.getText().toString().isEmpty()) return false;
        return  true;
    }
    protected boolean isStrongPassword(String myPass) {
        if(!myPass.matches(".*[a-z].*")) return  false;
        if(!myPass.matches(".*[0-9].*")) return  false;
        if(myPass.length()<=5) return false;
        return true;
    }

    private void saveIdAndPass(String phone, String pass) {
        sPref = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString(PHONE_NUMBER, phone);
        editor.putString(PASS, pass);
        editor.apply();
    }

    private void attentionThisUserAlreadyReg(){
        Toast.makeText(
                this,
                "Данный пользователь уже зарегестрирован.",
                Toast.LENGTH_SHORT).show();
    }

    private void attentionThisCodeWasWrong(){
        Toast.makeText(
                this,
                "Код введен неверно",
                Toast.LENGTH_SHORT).show();
    }

    private  void goToRegistration(String phoneNumber){
        Intent intent = new Intent(this, Registration.class);
        intent.putExtra(PHONE_NUMBER, phoneNumber);
        startActivity(intent);
        finish();
    }

    private void attentionBadConnection() {
        Toast.makeText(this,"Плохое соединение",Toast.LENGTH_SHORT).show();
    }
}