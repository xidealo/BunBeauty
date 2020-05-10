package com.bunbeauty.ideal.myapplication.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.ideal.myapplication.R;
import com.bunbeauty.ideal.myapplication.other.ISwitcher;

@SuppressLint("ValidFragment")
public class SwitcherElement extends Fragment implements View.OnClickListener {

    private static final String TAG = "DBInf";

    private Button leftBtn;
    private Button rightBtn;

    private LinearLayout mainLayout;

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
        switcher = (ISwitcher) this.getContext();

        leftBtn = view.findViewById(R.id.leftSwitcherElementBtn);
        rightBtn = view.findViewById(R.id.rightSwitcherElementBtn);
        mainLayout = view.findViewById(R.id.switcherElementLayout);
        leftBtn.setOnClickListener(this);
        rightBtn.setOnClickListener(this);

        setData();
    }

    private void setData() {
        leftBtn.setText(leftBtnText);
        rightBtn.setText(rightBtnText);
    }

    public void showSwitcherElement() {
        mainLayout.setVisibility(View.VISIBLE);
    }

    public void hideSwitcherElement() {
        mainLayout.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.leftSwitcherElementBtn:
                if (!isLeft) {
                    isLeft = true;
                    switcher.firstSwitcherAct();

                }
                break;

            case R.id.rightSwitcherElementBtn:
                if (isLeft) {
                    isLeft = false;
                    switcher.secondSwitcherAct();

                }
                break;

            default:
                break;
        }
    }
}