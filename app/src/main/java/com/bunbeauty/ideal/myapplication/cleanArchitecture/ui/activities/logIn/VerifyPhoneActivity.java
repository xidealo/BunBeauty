package com.bunbeauty.ideal.myapplication.cleanArchitecture.ui.activities.logIn;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ideal.myapplication.R;
import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.VerifyPhoneInteractor;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.VerifyCallback;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppModule;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerAppComponent;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.VerifyPhonePresenter;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.VerifyPhoneView;
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithViewApi;

import javax.inject.Inject;

public class VerifyPhoneActivity extends MvpAppCompatActivity implements View.OnClickListener, VerifyCallback, VerifyPhoneView {

    private static final String TAG = "DBInf";

    private Button verifyCodeBtn;
    private TextView resendCodeText;
    private EditText codeInput;
    private TextView changePhoneText;
    private TextView alertCodeText;
    private ProgressBar progressBar;

    @Inject
    VerifyPhoneInteractor verifyPhoneInteractor;

    @InjectPresenter
    VerifyPhonePresenter verifyPhonePresenter;

    @ProvidePresenter
    VerifyPhonePresenter provideVerifyPhonePresenter() {
        DaggerAppComponent.builder()
                .appModule(new AppModule(getApplication()))
                .build()
                .inject(this);
        return new VerifyPhonePresenter(verifyPhoneInteractor);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify_phone);

        initView();
        verifyPhonePresenter.sendCode(this);
    }

    private void initView() {
        Log.d(TAG, "initView RegistrationActivity: ");

        verifyCodeBtn = findViewById(R.id.verifyVerifyBtn);
        resendCodeText = findViewById(R.id.resendVerifyText);
        alertCodeText = findViewById(R.id.alertCodeVerifyText);
        codeInput = findViewById(R.id.codeVerifyInput);
        changePhoneText = findViewById(R.id.changePhoneVerifyText);

        progressBar = findViewById(R.id.progressBarVerifyCode);

        verifyCodeBtn.setOnClickListener(this);
        resendCodeText.setOnClickListener(this);
        changePhoneText.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        WorkWithViewApi.hideKeyboard(this);
        switch (v.getId()) {
            case R.id.verifyVerifyBtn:
                verifyPhonePresenter.verify(codeInput.getText().toString(), this);
                break;
            case R.id.resendVerifyText:
                verifyPhonePresenter.resendCode(this);
                break;
            case R.id.changePhoneVerifyText:
                goBackToAuthorization();
                break;
            default:
                break;
        }
    }

    @Override
    public void hideViewsOnScreen() {
        verifyCodeBtn.setVisibility(View.GONE);
        resendCodeText.setVisibility(View.GONE);
        codeInput.setVisibility(View.GONE);
        changePhoneText.setVisibility(View.GONE);
        alertCodeText.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void showViewsOnScreen() {
        verifyCodeBtn.setVisibility(View.VISIBLE);
        resendCodeText.setVisibility(View.VISIBLE);
        codeInput.setVisibility(View.VISIBLE);
        changePhoneText.setVisibility(View.VISIBLE);
        alertCodeText.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showResendCode() {
        Toast.makeText(this, "Код был отправлен", Toast.LENGTH_LONG).show();
    }

    @Override
    public void showWrongCode() {
        Toast.makeText(this, "Вы ввели неверный код", Toast.LENGTH_LONG).show();
    }

    @Override
    public void callbackWrongCode() {
        showViewsOnScreen();
        showWrongCode();
        codeInput.setError("Неправильный код");
        codeInput.requestFocus();
    }

    private void goBackToAuthorization() {
        super.onBackPressed();
    }

}