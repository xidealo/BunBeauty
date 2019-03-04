package com.example.ideal.myapplication.logIn;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.ideal.myapplication.R;
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

public class CreatingNewPassword extends AppCompatActivity implements View.OnClickListener {

    private static final String PHONE_NUMBER = "Phone number";
    private static final String USERS = "users";
    private static final String PASS = "password";

    Button changeBtn;

    EditText passInput;
    EditText repeatPassInput;

    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.creating_new_password);

        changeBtn = findViewById(R.id.changeCreatingNewPassBtn);
        passInput = findViewById(R.id.passCreatingNewPassInput);
        repeatPassInput = findViewById(R.id.repeatCreatingNewPassInput);

        changeBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.changeCreatingNewPassBtn:
                if(checkPassword()) {
                    saveNewPassword();
                    goToAuthorization();
                }
                break;
        }
    }

    private boolean checkPassword() {
        password = passInput.getText().toString();

        if (!isStrongPassword(password)) {
            passInput.setError("Weak password...");
            passInput.requestFocus();
            return false;
        }

        String reapeatPassword = repeatPassInput.getText().toString();
        if (!password.equals(reapeatPassword)) {
            repeatPassInput.setError("Passwords do not match...");
            repeatPassInput.requestFocus();
            return false;
        }

        return true;
    }

    private void saveNewPassword() {
        String phone = getIntent().getStringExtra(PHONE_NUMBER);
        password = encryptThisStringSHA512(password);

        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(USERS).child(phone);

        Map<String,Object> items = new HashMap<>();
        items.put(PASS, password);
        userRef.updateChildren(items);
    }

    protected boolean isStrongPassword(String myPass) {
        if(!myPass.matches(".*[a-z].*")) return  false;
        if(!myPass.matches(".*[0-9].*")) return  false;
        if(myPass.length()<=5) return false;
        return true;
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


    private  void  goToAuthorization(){
        Intent intent = new Intent(this, Authorization.class);
        startActivity(intent);
        finish();
    }
}
