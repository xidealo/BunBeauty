package com.bunbeauty.ideal.myapplication.logIn;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.ideal.myapplication.R;
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithViewApi;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Authorization extends AppCompatActivity implements View.OnClickListener {

    private static final String PHONE_NUMBER = "Phone number";
    private Button verifyBtn;
    private TextView enterPhoneText;
    private EditText phoneInput;
    private Spinner codeSpinner;
    private String myPhoneNumber;
    private ProgressBar progressBar;
    private static final String TAG = "DBInf";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authorization);

        verifyBtn = findViewById(R.id.verifyAuthBtn);
        phoneInput = findViewById(R.id.phoneAuthInput);
        enterPhoneText = findViewById(R.id.titleAuthText);
        progressBar = findViewById(R.id.progressBarAuthorization);

        codeSpinner = findViewById(R.id.codeAuthSpinner);

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
                String countryCode = CountryCodes.codes[codeSpinner.getSelectedItemPosition()];
                myPhoneNumber = countryCode + phoneInput.getText().toString();
                if(isPhoneCorrect(myPhoneNumber.trim())) {
                    verifyBtn.setClickable(false);
                    goToVerifyPhone();
                }
                break;

            default:
                break;
        }
    }

    protected Boolean isPhoneCorrect(String myPhoneNumber){

        if(myPhoneNumber.length() < 11 || myPhoneNumber.length() > 12) {
            phoneInput.setError("Некорректный номер");
            phoneInput.requestFocus();
            return false;
        }
        return  true;
    }

    private void hideViewsOfScreen(){
        progressBar.setVisibility(View.VISIBLE);
        codeSpinner.setVisibility(View.GONE);
        phoneInput.setVisibility(View.GONE);
        verifyBtn.setVisibility(View.GONE);
    }

    private void showViewsOnScreen(){
        codeSpinner.setVisibility(View.VISIBLE);
        enterPhoneText.setVisibility(View.VISIBLE);
        phoneInput.setVisibility(View.VISIBLE);
        verifyBtn.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        verifyBtn.setClickable(true);
    }

    private void goToVerifyPhone() {
        Intent intent = new Intent(this, VerifyPhone.class);
        intent.putExtra(PHONE_NUMBER, myPhoneNumber);
        startActivity(intent);
    }

}