package com.bunbeauty.ideal.myapplication.fragments.panelElements;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ideal.myapplication.R;
import com.bunbeauty.ideal.myapplication.chat.Messages;
import com.bunbeauty.ideal.myapplication.editing.EditProfile;
import com.bunbeauty.ideal.myapplication.editing.EditService;
import com.bunbeauty.ideal.myapplication.helpApi.SubscriptionsApi;
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithLocalStorageApi;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.db.DBHelper;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.presentation.profile.ProfileActivity;
import com.bunbeauty.ideal.myapplication.searchService.GuestService;
import com.bunbeauty.ideal.myapplication.searchService.MainScreen;
import com.bunbeauty.ideal.myapplication.searchService.SearchService;


public class TopPanel extends Fragment implements View.OnClickListener {

    private static final String TAG = "DBInf";

    private static final String USER_NAME = "my name";
    private static final String USER_CITY = "my city";
    private static final String OWNER_ID = "owner id";
    private static final String SERVICE_ID = "service id";

    private TextView backText;
    private TextView titleText;
    private TextView searchText;
    private TextView countOfSubsText;
    private ImageView avatarImage;
    private LinearLayout avatarTopPanelLayout;
    private ImageView logoImage;
    private TextView settingText;
    private TextView subscribeText;
    private TextView unsubscribeText;

    private String title;
    private boolean isMyProfile;
    private boolean isMyService;
    private String serviceId;
    private String serviceOwnerId;

    private SubscriptionsApi subsApi;

    public TopPanel() {
    }

    @SuppressLint("ValidFragment")
    // Для остальных
    public TopPanel(String _title) {
        title = _title;
        isMyProfile = false;
        isMyService = false;
        serviceId = null;
        serviceOwnerId = null;
    }

    @SuppressLint("ValidFragment")
    // Для моего профиля
    public TopPanel(String _title, boolean _isMyProfile) {
        title = _title;
        isMyProfile = _isMyProfile;
        isMyService = false;
        serviceId = null;
        serviceOwnerId = null;
    }

    @SuppressLint("ValidFragment")
    // Для Messages
    public TopPanel(String _title, String _serviceOwnerId) {
        title = _title;
        isMyProfile = false;
        isMyService = false;
        serviceId = null;
        serviceOwnerId = _serviceOwnerId;
    }

    @SuppressLint("ValidFragment")
    // Для сервиса
    public TopPanel(String _title, boolean _isMyService, String _serviceId, String _serviceOwnerId) {
        title = _title;
        isMyProfile = false;
        isMyService = _isMyService;
        serviceId = _serviceId;
        serviceOwnerId = _serviceOwnerId;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.top_panel, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        backText = view.findViewById(R.id.backTopPanelText);
        titleText = view.findViewById(R.id.titleTopPanelText);
        countOfSubsText = view.findViewById(R.id.countOfSubsTopPanelText);
        settingText = view.findViewById(R.id.settingTopPanelText);
        searchText = view.findViewById(R.id.searchTopPanelText);
        subscribeText = view.findViewById(R.id.subscribeTopPanelText);
        unsubscribeText = view.findViewById(R.id.unsubscribeTopPanelText);
        logoImage = view.findViewById(R.id.logoTopPanelImage);

        avatarImage = view.findViewById(R.id.avatarTopPanelImage);
        avatarTopPanelLayout = view.findViewById(R.id.avatarTopPanelLayout);

        if (super.getActivity().isTaskRoot()) {
            backText.setVisibility(View.INVISIBLE);
        } else {
            backText.setOnClickListener(this);
        }

        settingText.setOnClickListener(this);
        subscribeText.setOnClickListener(this);
        unsubscribeText.setOnClickListener(this);
        // Если не мой профиль
        if (!isMyProfile) {
            // Если это профиль
            if (getContext().getClass() == ProfileActivity.class) {
                ProfileActivity profileActivity = (ProfileActivity) super.getActivity();
                String ownerId = profileActivity.getIntent().getStringExtra(OWNER_ID);
                subsApi = new SubscriptionsApi(ownerId, getContext());
                subsApi.loadCountOfSubscribers(countOfSubsText);

                if (((ProfileActivity) super.getActivity()).checkSubscription()) {
                    setSubscribe();
                } else {
                    setUnsubscribe();
                }
                //убираем значок настроек
                settingText.setVisibility(View.GONE);
                countOfSubsText.setVisibility(View.VISIBLE);
            } else {
                // Если это страница сервиса
                if (getContext().getClass() == GuestService.class) {
                    // Если это мой сервис
                    if (isMyService) {
                        // Если это чужой сервис
                    } else {
                        DBHelper dbHelper = new DBHelper(getContext());
                        SQLiteDatabase database = dbHelper.getReadableDatabase();
                        WorkWithLocalStorageApi workWithLocalStorageApi = new WorkWithLocalStorageApi(database);
                        int width = getResources().getDimensionPixelSize(R.dimen.photo_avatar_width);
                        int height = getResources().getDimensionPixelSize(R.dimen.photo_avatar_height);
                        avatarTopPanelLayout.setVisibility(View.VISIBLE);
                        workWithLocalStorageApi.setPhotoAvatar(serviceOwnerId,avatarImage,width,height);
                        settingText.setVisibility(View.GONE);
                        avatarImage.setOnClickListener(this);
                    }
                    // Если это не сервис
                } else {
                    // Если это редактирование сервиса
                    if (getContext().getClass() == Messages.class) {
                        // Если это диалог
                        DBHelper dbHelper = new DBHelper(getContext());
                        SQLiteDatabase database = dbHelper.getReadableDatabase();

                        WorkWithLocalStorageApi workWithLocalStorageApi = new WorkWithLocalStorageApi(database);
                        int width = getResources().getDimensionPixelSize(R.dimen.photo_avatar_width);
                        int height = getResources().getDimensionPixelSize(R.dimen.photo_avatar_height);
                        workWithLocalStorageApi.setPhotoAvatar(serviceOwnerId,avatarImage,width,height);
                        avatarTopPanelLayout.setVisibility(View.VISIBLE);
                        settingText.setVisibility(View.GONE);
                        avatarImage.setOnClickListener(this);
                        // Если это не диалог
                    } else {
                        if (getContext().getClass() == EditService.class) {
                            //multiBtn.setText("Удалить");
                            //Пока что не будет удаления сервиса, потому что много связей и тяжело удалить
                            settingText.setVisibility(View.INVISIBLE);
                            // Если это не редактирование сервиса
                        } else {
                            // Если это главная страница
                            if (getContext().getClass() == MainScreen.class) {
                                settingText.setVisibility(View.GONE);
                                searchText.setVisibility(View.VISIBLE);
                                searchText.setOnClickListener(this);
                                // Если это любое другое активити
                            } else {
                                settingText.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                }
            }
        }
        setTitle();
    }

    private void setTitle() {
        //если на главной странице, то устанавливаем logo
        if(title.equals("Главная")){
            titleText.setVisibility(View.GONE);
            logoImage.setVisibility(View.VISIBLE);
        }
        else {
            titleText.setText(title.toUpperCase());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backTopPanelText:
                super.getActivity().onBackPressed();
                break;

            case R.id.settingTopPanelText:
                multiClick();
                break;

            case R.id.searchTopPanelText:
                goToSearchService();
                break;

            case R.id.subscribeTopPanelText:
                checkSubscribe();
                break;

            case R.id.unsubscribeTopPanelText:
                checkSubscribe();
                break;

            case R.id.avatarTopPanelImage:
                goToProfile();
                break;
        }
    }

    private void multiClick() {
        // мой профиль
        if (isMyProfile) {
            goToEditProfile();
            return;
        }
        Class currentClass = getContext().getClass();
        // сервис
        if (currentClass == GuestService.class) {
            if (isMyService) {
                goToEditService();
                return;
            } else {
                goToProfile();
                return;
            }
        }

        // Сообщения
        if (currentClass == Messages.class) {
            goToProfile();
            return;
        }

        // редактирование сервиса
        if (currentClass == EditService.class) {
            deleteService();
            return;
        }
    }

    private void checkSubscribe(){
        Class currentClass = getContext().getClass();
        //чужой профиль
        if (currentClass == ProfileActivity.class) {
            if ((boolean) subscribeText.getTag()) {
                setUnsubscribe();
                subsApi.unsubscribe();
                Toast.makeText(getContext(), "Подписка отменена", Toast.LENGTH_SHORT).show();

            } else {
                setSubscribe();
                subsApi.subscribe();
                Toast.makeText(getContext(), "Вы подписались", Toast.LENGTH_SHORT).show();
            }

            return;
        }
    }
    private void setSubscribe() {
        unsubscribeText.setVisibility(View.GONE);
        subscribeText.setVisibility(View.VISIBLE);
        subscribeText.setTag(true);
    }

    private void setUnsubscribe() {
        subscribeText.setVisibility(View.GONE);
        unsubscribeText.setVisibility(View.VISIBLE);
        subscribeText.setTag(false);
    }

    private void goToEditProfile() {
        Intent intent = new Intent(getContext(), EditProfile.class);
        TextView nameText = super.getActivity().findViewById(R.id.nameProfileText);
        TextView cityText = super.getActivity().findViewById(R.id.cityProfileText);
        intent.putExtra(USER_NAME, nameText.getText());
        intent.putExtra(USER_CITY, cityText.getText());
        startActivity(intent);
    }

    private void goToEditService() {
        Intent intent = new Intent(getContext(), EditService.class);
        intent.putExtra(SERVICE_ID, serviceId);

        startActivity(intent);
    }

    private void goToProfile() {
        Intent intent = new Intent(getContext(), ProfileActivity.class);
        intent.putExtra(OWNER_ID, serviceOwnerId);

        startActivity(intent);
    }

    private void goToSearchService() {
        Intent intent = new Intent(getContext(), SearchService.class);
        startActivity(intent);
    }

    private void deleteService() {
        EditService activity = (EditService) this.getActivity();
        activity.deleteThisService();
    }

}
