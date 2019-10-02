package com.bunbeauty.ideal.myapplication.cleanArchitecture.ui.activities.logIn;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.ideal.myapplication.R;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.AuthorizationInteractor;
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithViewApi;
import com.bunbeauty.ideal.myapplication.logIn.CountryCodes;
import com.bunbeauty.ideal.myapplication.logIn.MyAuthorization;

public class AuthorizationActivity extends AppCompatActivity implements View.OnClickListener {

    private Button verifyBtn;
    private TextView enterPhoneText;
    private EditText phoneInput;
    private Spinner codeSpinner;
    private String myPhoneNumber;
    private ProgressBar progressBar;
    private static final String TAG_UI = "tag_ui";
    private AuthorizationInteractor authorizationInteractor;

    // Это активити
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authorization);

        initView();
        //business logic class
        if (authorizationInteractor.getCurrentFbUser() != null) {
            hideViewsOfScreen();
            myPhoneNumber = authorizationInteractor.getCurrentFbUser().getPhoneNumber();
            MyAuthorization myAuth = new MyAuthorization(AuthorizationActivity.this, myPhoneNumber);
            myAuth.authorizeUser();
        } else {
            showViewsOnScreen();
        }
        WorkWithViewApi.hideKeyboard(this);
    }

    private void initView(){

        verifyBtn = findViewById(R.id.verifyAuthBtn);
        phoneInput = findViewById(R.id.phoneAuthInput);
        enterPhoneText = findViewById(R.id.titleAuthText);
        progressBar = findViewById(R.id.progressBarAuthorization);
        codeSpinner = findViewById(R.id.codeAuthSpinner);

        authorizationInteractor = new AuthorizationInteractor();

        verifyBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.verifyAuthBtn:
                String countryCode = CountryCodes.codes[codeSpinner.getSelectedItemPosition()];
                myPhoneNumber = countryCode + phoneInput.getText().toString();
                if (authorizationInteractor.isPhoneCorrect(myPhoneNumber.trim())) {
                    enableVerifyBtn(false);
                    authorizationInteractor.goToVerifyPhone(myPhoneNumber,this);
                }
                else {
                  setPhoneError();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        enableVerifyBtn(true);
    }

    private void hideViewsOfScreen() {
        Log.d(TAG_UI, "hideViewsOfScreen");
        progressBar.setVisibility(View.VISIBLE);
        codeSpinner.setVisibility(View.GONE);
        phoneInput.setVisibility(View.GONE);
        verifyBtn.setVisibility(View.GONE);
    }

    private void showViewsOnScreen() {
        Log.d(TAG_UI, "showViewsOnScreen: ");
        codeSpinner.setVisibility(View.VISIBLE);
        enterPhoneText.setVisibility(View.VISIBLE);
        phoneInput.setVisibility(View.VISIBLE);
        verifyBtn.setVisibility(View.VISIBLE);
    }

    private void setPhoneError(){
        Log.d(TAG_UI, "setPhoneError: ");
        phoneInput.setError("Некорректный номер");
        phoneInput.requestFocus();
    }

    private void enableVerifyBtn(Boolean status){
        Log.d(TAG_UI, "enableVerifyBtn: " + status);
        verifyBtn.setClickable(status);
    }
}