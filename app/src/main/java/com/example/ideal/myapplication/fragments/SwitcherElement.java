package com.example.ideal.myapplication.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.other.ISwitcher;

@SuppressLint("ValidFragment")
public class SwitcherElement extends Fragment implements View.OnClickListener {

    private static final String TAG = "DBInf";

    private Button leftBtn;
    private Button rightBtn;

    private String leftBtnText;
    private String rightBtnText;
    private boolean isLeft = true;
    private ISwitcher switcher;

    @SuppressLint("ValidFragment")
    public SwitcherElement(String _leftBtnText, String _rightBtnText) {
        leftBtnText = _leftBtnText;
        rightBtnText = _rightBtnText;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.swicher_element, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        switcher = (ISwitcher)this.getContext();

        leftBtn = view.findViewById(R.id.leftSwicherElementBtn);
        rightBtn = view.findViewById(R.id.rightSwicherElementBtn);

        leftBtn.setOnClickListener(this);
        rightBtn.setOnClickListener(this);

        setData();
    }

    private void setData() {
        leftBtn.setText(leftBtnText);
        rightBtn.setText(rightBtnText);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.leftSwicherElementBtn:
                if (!isLeft) {
                    isLeft = true;
                    switcher.firstSwitcherAct();
                    leftBtn.setBackgroundResource(R.drawable.left_transparent_btn);
                    rightBtn.setBackgroundResource(R.drawable.right_btn);
                }
                break;

            case R.id.rightSwicherElementBtn:
                if (isLeft) {
                    isLeft = false;
                    switcher.secondSwitcherAct();
                    leftBtn.setBackgroundResource(R.drawable.left_btn);
                    rightBtn.setBackgroundResource(R.drawable.right_transparent_btn);
                }
                break;

            default:
                break;
        }
    }
}