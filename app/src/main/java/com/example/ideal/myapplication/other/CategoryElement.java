package com.example.ideal.myapplication.other;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.ideal.myapplication.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

@SuppressLint("ValidFragment")
public class CategoryElement extends Fragment implements View.OnClickListener {

    private static final String TAG = "DBInf";

    private LinearLayout tagsMinLayout;
    private LinearLayout tagsMaxLayout;
    private Spinner categorySpinner;

    private ArrayList<String> selectedTagsArray;
    private ArrayList<String> inputTagsArray;
    private int categoryPosition;
    private int categoryIndex = 0;
    private Context context;

    @SuppressLint("ValidFragment")
    public CategoryElement(Context context) {
        this.context = context;
        selectedTagsArray = new ArrayList<>();
        inputTagsArray = new ArrayList<>();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.category_element, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        categorySpinner = view.findViewById(R.id.categorySpinner);
        tagsMinLayout = view.findViewById(R.id.tagsMinLayout);
        tagsMaxLayout = view.findViewById(R.id.tagsMaxLayout);
        Button tagsBtn = view.findViewById(R.id.tagsBtn);
        Button minimizeBtn = view.findViewById(R.id.minimizeTagsBtn);

        categorySpinner.setSelection(categoryIndex);
        setCategorySpinnerListener();
        tagsBtn.setOnClickListener(this);
        minimizeBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tagsBtn:
                selectedTagsArray.addAll(inputTagsArray);
                inputTagsArray.clear();
                showTags();
                break;
            case R.id.minimizeTagsBtn:
                hideTags();
                break;
            default:
                tagClick((TextView) v);
                break;
        }
    }

    private void setCategorySpinnerListener() {
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoryPosition = position;
                selectedTagsArray.clear();
                hideTags();
                if (position == 0) {
                    tagsMinLayout.setVisibility(View.GONE);
                } else {
                    tagsMinLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void hideTags() {
        tagsMaxLayout.removeViews(1, tagsMaxLayout.getChildCount() - 2);

        tagsMinLayout.setVisibility(View.VISIBLE);
        tagsMaxLayout.setVisibility(View.GONE);
    }

    private void showTags() {
        CharSequence[] tagsArray = getResources()
                .obtainTypedArray(R.array.tags_references)
                .getTextArray(categoryPosition - 1);

        for (CharSequence tag : tagsArray) {
            TextView tagText = new TextView(context);
            tagText.setText(tag);
            tagText.setGravity(Gravity.CENTER);
            tagText.setTypeface(ResourcesCompat.getFont(context, R.font.roboto_bold));
            tagText.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            tagText.setOnClickListener(this);
            tagText.setPadding(0, 16, 0, 16);
            if (selectedTagsArray.contains(tag.toString())) {
                tagText.setBackgroundResource(R.drawable.category_button_pressed);
                tagText.setTextColor(Color.BLACK);
            }

            tagsMaxLayout.addView(tagText, tagsMaxLayout.getChildCount() - 1);
        }

        tagsMinLayout.setVisibility(View.GONE);
        tagsMaxLayout.setVisibility(View.VISIBLE);
    }

    private void tagClick(TextView tagText) {
        String text = tagText.getText().toString();
        if (selectedTagsArray.contains(text)) {
            tagText.setBackgroundResource(0);
            selectedTagsArray.remove(text);
        } else {
            tagText.setBackgroundResource(R.drawable.category_button_pressed);
            selectedTagsArray.add(text);
        }
    }

    public String getCategory() {
        return categorySpinner.getSelectedItem().toString().toLowerCase().trim();
    }

    public void setCategory(int index) {
        categoryIndex = index;
    }

    public ArrayList<String> getTagsArray() {
        return selectedTagsArray;
    }

    public void addTag(String tag) {
        Log.d(TAG, "addTag: " + tag);
        inputTagsArray.add(tag);
    }
}