package com.example.ideal.myapplication.logIn;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.fragments.objects.Photo;
import com.example.ideal.myapplication.fragments.objects.Service;
import com.example.ideal.myapplication.fragments.objects.User;
import com.example.ideal.myapplication.helpApi.WorkWithViewApi;
import com.example.ideal.myapplication.other.DBHelper;
import com.example.ideal.myapplication.other.Profile;
import com.example.ideal.myapplication.helpApi.DownloadServiceData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import com.example.ideal.myapplication.helpApi.WorkWithViewApi;
import com.example.ideal.myapplication.other.DBHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthProvider;


public class Authorization extends AppCompatActivity implements View.OnClickListener {

    private static final String FILE_NAME = "Info";
    private static final String PHONE_NUMBER = "Phone number";
    private static final String PASS = "password";
    //PHOTOS
    private static final String PHOTOS = "photos";
    private static final String PHOTO_LINK = "photo link";
    private static final String OWNER_ID = "owner id";

    private static final String USERS = "users";
    private static final String NAME = "name";
    private static final String CITY = "city";

    private static final String SERVICES = "services";
    private static final String USER_ID = "user id";
    private static final String DESCRIPTION = "description";
    private static final String COST = "cost";

    private static final String WORKING_TIME = "working time";
    private static final String WORKING_DAY_ID = "working day id";
    private static final String TIME = "time";

    private static final String WORKING_DAYS = "working days";
    private static final String SERVICE_ID = "service id";
    private static final String DATE = "data";
    private static final String TAG = "DBInf";

    private Button verifyBtn;
    private EditText phoneInput;

    private Spinner codeSpinner;

    private long orderCounter;
    private String myPhoneNumber;

    DBHelper dbHelper;
    FirebaseAuth fbAuth;
    private String phoneVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authorization);

        dbHelper = new DBHelper(this);
        fbAuth = FirebaseAuth.getInstance();

        verifyBtn = findViewById(R.id.verifyAuthBtn);
        phoneInput = findViewById(R.id.phoneAuthInput);

        codeSpinner = findViewById(R.id.codeAuthSpinner);
        codeSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, CountryCodes.countryNames));

        verifyBtn.setOnClickListener(this);

        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser != null) {
            hideViewsOfScreen();

            myPhoneNumber = fbUser.getPhoneNumber();

            MyAuthorization myAuth = new MyAuthorization(Authorization.this, myPhoneNumber);
            myAuth.authorizeUser();
        } else {
            showViewsOnScreen();
        }
        WorkWithViewApi.hideKeyboard(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case  R.id.verifyAuthBtn:
                myPhoneNumber = phoneInput.getText().toString();
                if(isPhoneCorrect()) {
                    verifyBtn.setClickable(false);
                    String countryCode = CountryCodes.codes[codeSpinner.getSelectedItemPosition()];
                    myPhoneNumber = countryCode + phoneInput.getText().toString();

                   goToVerifyPhone();
                }
                break;

            default:
                break;
        }
    }

    protected Boolean isPhoneCorrect(){
        if(myPhoneNumber.length() < 9 || myPhoneNumber.length() > 10) {
            phoneInput.setError("Некорректный номер");
            phoneInput.requestFocus();
            return false;
        }
        return  true;
    }

    private void hideViewsOfScreen(){
        codeSpinner.setVisibility(View.INVISIBLE);

        phoneInput.setVisibility(View.INVISIBLE);

        verifyBtn.setVisibility(View.INVISIBLE);
    }

    private void showViewsOnScreen(){
        codeSpinner.setVisibility(View.VISIBLE);

        phoneInput.setVisibility(View.VISIBLE);

        verifyBtn.setVisibility(View.VISIBLE);
    }

    private void goToVerifyPhone() {
        Intent intent = new Intent(this, VerifyPhone.class);
        intent.putExtra(PHONE_NUMBER, myPhoneNumber);
        startActivity(intent);
    }
}