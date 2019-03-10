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
import com.example.ideal.myapplication.helpApi.WorkWithViewApi;
import com.example.ideal.myapplication.other.DBHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Authorization extends AppCompatActivity implements View.OnClickListener {

    private static final String PHONE_NUMBER = "Phone number";


    private Button verifyBtn;
    private EditText phoneInput;

    private Spinner codeSpinner;

    private String myPhoneNumber;

    DBHelper dbHelper;
    FirebaseAuth fbAuth;


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