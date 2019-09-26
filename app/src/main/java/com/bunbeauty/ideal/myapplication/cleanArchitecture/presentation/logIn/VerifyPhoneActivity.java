package com.bunbeauty.ideal.myapplication.cleanArchitecture.presentation.logIn;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ideal.myapplication.R;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.VerifyPhoneInteractor;
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithViewApi;

public class VerifyPhoneActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "DBInf";

    private Button verifyCodeBtn;
    private TextView resendCodeText;
    private EditText codeInput;
    private TextView changePhoneText;
    private TextView alertCodeText;
    private ProgressBar progressBar;
    private VerifyPhoneInteractor verifyPhoneInteractor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify_phone);
        initView();
        verifyPhoneInteractor.sendVerificationCode(verifyPhoneInteractor.getMyPhoneNumber(getIntent()),this);
    }

    private void initView() {
        verifyCodeBtn = findViewById(R.id.verifyVerifyBtn);
        resendCodeText = findViewById(R.id.resendVerifyText);
        alertCodeText = findViewById(R.id.alertCodeVerifyText);
        codeInput = findViewById(R.id.codeVerifyInput);
        changePhoneText = findViewById(R.id.changePhoneVerifyText);

        verifyPhoneInteractor= new VerifyPhoneInteractor();
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
                String code = codeInput.getText().toString();
                if (code.trim().length() >= 6) {
                    // подтверждаем код и если все хорошо, создаем юзера
                    verifyPhoneInteractor.verifyCode(code);
                    hideViewsOfScreen();

                    // спрашиваем ответ
                }
                break;
            case R.id.resendVerifyText:
                if (verifyPhoneInteractor.getResendToken() != null) {
                    assertResendCode();
                    verifyPhoneInteractor.resendVerificationCode(verifyPhoneInteractor.getMyPhoneNumber(getIntent()),
                            this,verifyPhoneInteractor.getResendToken());
                }
                break;
            case R.id.changePhoneVerifyText:
                goBackToAuthorization();
                break;
            default:
                break;
        }
    }

    private void hideViewsOfScreen() {
        verifyCodeBtn.setVisibility(View.GONE);
        resendCodeText.setVisibility(View.GONE);
        codeInput.setVisibility(View.GONE);
        changePhoneText.setVisibility(View.GONE);
        alertCodeText.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void showViewsOnScreen() {
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