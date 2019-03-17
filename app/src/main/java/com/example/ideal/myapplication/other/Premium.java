package com.example.ideal.myapplication.other;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.helpApi.PanelBuilder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Premium extends AppCompatActivity implements View.OnClickListener {

    private static final String SERVICE_ID = "service id";
    private static final String SERVICES = "services";
    private static final String IS_PREMIUM = "is premium";

    private String serviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.premium);
        init();
    }

    private void init(){

        FragmentManager manager = getSupportFragmentManager();
        PanelBuilder panelBuilder = new PanelBuilder();
        panelBuilder.buildFooter(manager, R.id.footerPremiumLayout);
        panelBuilder.buildHeader(manager, "Премиум", R.id.headerPremiumLayout);

        Button getPremiumBtn = findViewById(R.id.setPremiumPremiumBtn);
        serviceId = getIntent().getStringExtra(SERVICE_ID);
        getPremiumBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        uploadServiceIsPremium(true);
    }

    private void uploadServiceIsPremium(boolean isPremium) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(SERVICES).child(serviceId);

        Map<String,Object> items = new HashMap<>();
        items.put(IS_PREMIUM,isPremium);

        myRef.updateChildren(items);

    }
}
