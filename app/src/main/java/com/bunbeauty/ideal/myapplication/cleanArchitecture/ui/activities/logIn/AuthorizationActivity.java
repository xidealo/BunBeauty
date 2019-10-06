package com.bunbeauty.ideal.myapplication.cleanArchitecture.ui.activities.logIn;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.ideal.myapplication.R;
import com.arellomobile.mvp.MvpAppCompatActivity;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppComponent;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerAppComponent;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.AuthorizationPresenter;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.AuthorizationView;
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithViewApi;
import com.bunbeauty.ideal.myapplication.logIn.CountryCodes;

import org.jetbrains.annotations.NotNull;

public class AuthorizationActivity extends MvpAppCompatActivity implements View.OnClickListener, AuthorizationView {

    private String PHONE_NUMBER = "phone number";

    private Button verifyBtn;
    private TextView enterPhoneText;
    private EditText phoneInput;
    private Spinner codeSpinner;
    private ProgressBar progressBar;
    private static final String TAG_UI = "tag_ui";

    //@InjectPresenter
    AuthorizationPresenter authorizationPresenter;

    private AppComponent appComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appComponent = DaggerAppComponent.builder().build();
        appComponent.inject(this);
        authorizationPresenter = new AuthorizationPresenter();

        setContentView(R.layout.authorization);
        initView();

        authorizationPresenter.authorize();
    }

    private void initView() {
        verifyBtn = findViewById(R.id.verifyAuthBtn);
        phoneInput = findViewById(R.id.phoneAuthInput);
        enterPhoneText = findViewById(R.id.titleAuthText);
        progressBar = findViewById(R.id.progressBarAuthorization);
        codeSpinner = findViewById(R.id.codeAuthSpinner);
        verifyBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.verifyAuthBtn) {
            authorizationPresenter.authorize(
                    CountryCodes.codes[codeSpinner.getSelectedItemPosition()] + phoneInput.getText().toString()
            );
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        enableVerifyBtn(true);
    }

    @Override
    public void hideViewsOnScreen() {
        Log.d(TAG_UI, "hideViewsOfScreen");
        progressBar.setVisibility(View.VISIBLE);
        codeSpinner.setVisibility(View.GONE);
        phoneInput.setVisibility(View.GONE);
        verifyBtn.setVisibility(View.GONE);
    }

    @Override
    public void showViewsOnScreen() {
        Log.d(TAG_UI, "showViewsOnScreen: ");
        codeSpinner.setVisibility(View.VISIBLE);
        enterPhoneText.setVisibility(View.VISIBLE);
        phoneInput.setVisibility(View.VISIBLE);
        verifyBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void setPhoneError() {
        Log.d(TAG_UI, "setPhoneError: ");
        phoneInput.setError("Некорректный номер");
        phoneInput.requestFocus();
    }

    @Override
    public void enableVerifyBtn(boolean status) {
        Log.d(TAG_UI, "enableVerifyBtn: " + status);
        verifyBtn.setClickable(status);
    }

    @Override
    public void goToVerifyPhone(@NotNull String myPhoneNumber) {
        Intent intent = new Intent(this, VerifyPhoneActivity.class);
        intent.putExtra(PHONE_NUMBER, myPhoneNumber);
        this.startActivity(intent);
    }

    @Override
    public void hideKeyboard() {
        WorkWithViewApi.hideKeyboard(this);
    }
}