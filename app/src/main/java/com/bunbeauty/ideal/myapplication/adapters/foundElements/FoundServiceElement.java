package com.bunbeauty.ideal.myapplication.adapters.foundElements;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import androidx.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.ideal.myapplication.R;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Service;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User;
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithLocalStorageApi;
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithStringsApi;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.DBHelper;
import com.bunbeauty.ideal.myapplication.searchService.GuestService;

public class FoundServiceElement implements View.OnClickListener {

    private final static String SERVICE_ID = "service id";
    private static final String TAG = "DBInf";

    private TextView nameUserText;
    private TextView cityText;
    private TextView nameServiceText;
    private TextView costText;
    private ImageView avatarImage;
    private RatingBar ratingBar;
    private LinearLayout layout;

    private float avgRating;

    private String serviceId;
    private String nameUserString;
    private String cityString;
    private String nameServiceString;
    private String costString;
    private String userId;
    private Context context;
    private View view;
    private boolean isPremium;

    public FoundServiceElement(Service service, User user, View view, Context context) {
        serviceId = service.getId();
        nameUserString = user.getName();
        cityString = user.getCity();
        nameServiceString = service.getName();
        costString = service.getCost();
        userId = user.getId();
        isPremium = service.getIsPremium();
        this.context = context;
        this.view= view;
        avgRating = service.getAverageRating();
    }

    public void createElement(){
        onViewCreated(view);
    }
    public void onViewCreated(@NonNull View view) {

        layout = view.findViewById(R.id.foundServiceElementLayout);
        nameUserText = view.findViewById(R.id.userNameFoundServiceElementText);
        cityText = view.findViewById(R.id.cityFoundServiceElementText);
        nameServiceText = view.findViewById(R.id.serviceNameFoundServiceElementText);
        costText = view.findViewById(R.id.costFoundServiceElementText);
        ratingBar = view.findViewById(R.id.ratingBarFondServiceElement);

        if (isPremium) {
            setPremiumBackground();

            ColorStateList sl  = ColorStateList.valueOf(context.getResources().getColor(R.color.panelColor));
            ratingBar.setProgressTintList(sl);
        }
        else {
            setDefaultBackground();
            ColorStateList sl  = ColorStateList.valueOf(context.getResources().getColor(R.color.yellow));
            ratingBar.setProgressTintList(sl);
        }

        avatarImage = view.findViewById(R.id.avatarFoundServiceElementImage);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 10, 10, 10);
        layout.setLayoutParams(params);

        layout.setOnClickListener(this);
        setData();
    }

    private void setData() {
        double inches = getScreenInches();
        //устанавливаем сокращения названий и имен в зависимости от размера экрана
        if(inches<5){
            nameUserText.setText(WorkWithStringsApi.cutString(nameUserString, 9));
            nameServiceText.setText(WorkWithStringsApi.cutString(nameServiceString.toUpperCase(), 14));
        }else{
            nameUserText.setText(WorkWithStringsApi.doubleCapitalSymbols(WorkWithStringsApi.cutString(nameUserString, 9)));
            nameServiceText.setText(WorkWithStringsApi.cutString(nameServiceString.toUpperCase(), 18));
        }
        cityText.setText(WorkWithStringsApi.firstCapitalSymbol(cityString));
        costText.setText("Цена \n" + costString);
        ratingBar.setRating(avgRating);

        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        int width = context.getResources().getDimensionPixelSize(R.dimen.photo_avatar_width);
        int height = context.getResources().getDimensionPixelSize(R.dimen.photo_avatar_height);

        WorkWithLocalStorageApi workWithLocalStorageApi = new WorkWithLocalStorageApi(database);
        workWithLocalStorageApi.setPhotoAvatar(userId, avatarImage, width, height);
    }

    @Override
    public void onClick(View v) {
        goToGuestService();
    }

    private void goToGuestService() {
        Intent intent = new Intent(context, GuestService.class);
        intent.putExtra(SERVICE_ID, serviceId);
        context.startActivity(intent);
    }

    private void setPremiumBackground() {
        layout.setBackgroundResource(R.drawable.block_text_premium);
        nameUserText.setBackgroundColor(context.getResources().getColor(R.color.yellow));
        cityText.setBackgroundColor(context.getResources().getColor(R.color.yellow));
        nameServiceText.setBackgroundColor(context.getResources().getColor(R.color.yellow));
        costText.setBackgroundColor(context.getResources().getColor(R.color.yellow));
        ratingBar.setBackgroundColor(context.getResources().getColor(R.color.yellow));
    }

    private void setDefaultBackground() {
        layout.setBackgroundResource(R.drawable.block_text);
        nameUserText.setBackgroundColor(context.getResources().getColor(R.color.white));
        cityText.setBackgroundColor(context.getResources().getColor(R.color.white));
        nameServiceText.setBackgroundColor(context.getResources().getColor(R.color.white));
        costText.setBackgroundColor(context.getResources().getColor(R.color.white));
        ratingBar.setBackgroundColor(context.getResources().getColor(R.color.white));
    }
    private double getScreenInches(){
        DisplayMetrics dm = new DisplayMetrics();

        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);

        windowManager.getDefaultDisplay().getMetrics(dm);
        int width=dm.widthPixels;
        int height=dm.heightPixels;
        double wi=(double)width/(double)dm.xdpi;
        double hi=(double)height/(double)dm.ydpi;
        double x = Math.pow(wi,2);
        double y = Math.pow(hi,2);
        return Math.sqrt(x+y)+0.3;
    }
}