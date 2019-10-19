package com.bunbeauty.ideal.myapplication.adapters.foundElements;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.android.ideal.myapplication.R;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Order;
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithStringsApi;
import com.bunbeauty.ideal.myapplication.searchService.GuestService;

public class FoundOrderElement implements View.OnClickListener {

    private final static String SERVICE_ID = "service id";

    private TextView nameText;
    private TextView timeText;

    private String orderId;
    private String orderName;
    private String orderDate;
    private String orderTime;
    private Context context;
    private View view;

    public FoundOrderElement(Order order, View view, Context context) {
        /*orderId = order.getOrderId();
        orderName = order.getOrderName();
        orderDate = order.getOrderDate();
        orderTime = order.getOrderTime();*/
        this.context = context;
        this.view = view;
    }

    public void createElement(){
        onViewCreated(view);
    }

    private void onViewCreated(@NonNull View view) {
        nameText = view.findViewById(R.id.nameFoundOrderElementText);
        timeText = view.findViewById(R.id.descriptionFoundOrderElementText);

        nameText.setOnClickListener(this);
        timeText.setOnClickListener(this);

        LinearLayout layout = view.findViewById(R.id.foundOrderProfileElementLayout);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 10, 10, 10);
        layout.setLayoutParams(params);

        layout.setOnClickListener(this);
        setData();
    }

    private void setData() {
        nameText.setText(WorkWithStringsApi.firstCapitalSymbol(WorkWithStringsApi.cutString(orderName,26)));
        timeText.setText(orderDate + " " + orderTime);
    }

    @Override
    public void onClick(View v) {
        goToGuestService();
    }

    private void goToGuestService(){
        Intent intent = new Intent(context, GuestService.class);
        intent.putExtra(SERVICE_ID, orderId);
        context.startActivity(intent);
    }
}