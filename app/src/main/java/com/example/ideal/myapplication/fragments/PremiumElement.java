package com.example.ideal.myapplication.fragments;

import android.annotation.SuppressLint;
import android.net.IpPrefix;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.other.IPremium;

public class PremiumElement extends Fragment implements View.OnClickListener {

    private static final String TAG = "DBInf";

    private IPremium iPremium;
    private String code;
    private TextView codeText;
    @SuppressLint("ValidFragment")
    public PremiumElement() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.premium_element, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        Button setPremiumBtn = view.findViewById(R.id.setPremiumPremiumElementBtn);
        codeText = view.findViewById(R.id.codePremiumElement);
        setPremiumBtn.setOnClickListener(this);
        iPremium = (IPremium) this.getContext();
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.setPremiumPremiumElementBtn) {
            code = codeText.getText().toString().toLowerCase().trim();
            iPremium.checkCode(code);
        }
    }
}
