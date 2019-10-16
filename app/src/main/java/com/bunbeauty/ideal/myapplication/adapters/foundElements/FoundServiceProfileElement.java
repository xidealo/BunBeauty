package com.bunbeauty.ideal.myapplication.adapters.foundElements;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.ideal.myapplication.R;
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithStringsApi;
import com.bunbeauty.ideal.myapplication.searchService.GuestService;

public class FoundServiceProfileElement implements View.OnClickListener {

    private static final String SERVICE_ID = "service id";
    private static final String TAG = "DBInf";

    private TextView nameText;
    private RatingBar ratingBar;

    private String idString;
    private String nameString;
    private float avgRating;
    private Context context;
    private View view;

    public FoundServiceProfileElement(Service service, View view, Context context) {
        idString = service.getId();
        nameString = service.getName();
        avgRating = service.getAverageRating();
        this.context = context;
        this.view= view;
    }

    public void createElement(){
        onViewCreated(view);
    }

    private void onViewCreated(View view) {
        nameText = view.findViewById(R.id.serviceNameFoundServiceProfileElementText);
        ratingBar = view.findViewById(R.id.ratingBarFondServiceProfileElement);
        nameText.setOnClickListener(this);
        LinearLayout layout = view.findViewById(R.id.foundServiceProfileElementLayout);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 10, 10, 10);
        layout.setLayoutParams(params);
        layout.setOnClickListener(this);
        setData();
    }

    private void setData() {
        nameText.setText(WorkWithStringsApi.firstCapitalSymbol(WorkWithStringsApi.cutString(nameString,26)));
        ratingBar.setRating(avgRating);
    }

    @Override
    public void onClick(View v) {
        goToGuestService();
    }

    private void goToGuestService(){
        Intent intent = new Intent(context, GuestService.class);
        intent.putExtra(SERVICE_ID, idString);
        context.startActivity(intent);
    }
}
