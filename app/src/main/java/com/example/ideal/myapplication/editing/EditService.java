package com.example.ideal.myapplication.editing;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.fragments.ServicePhotoElement;
import com.example.ideal.myapplication.fragments.objects.Photo;
import com.example.ideal.myapplication.fragments.objects.Service;
import com.example.ideal.myapplication.helpApi.PanelBuilder;
import com.example.ideal.myapplication.logIn.Authorization;
import com.example.ideal.myapplication.other.DBHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditService extends AppCompatActivity implements View.OnClickListener {

    private final String SERVICE_ID = "service id";
    private final String TAG = "DBInf";

    //services
    private final String SERVICES = "services";
    private final String NAME = "name";
    private final String COST = "cost";
    private final String DESCRIPTION = "description";
    private final String USER_ID = "user id";

    //working  days
    private final String WORKING_DAYS = "working days";
    private final String DATA = "data";
    private final String TIME = "time";

    //working time
    private static final String WORKING_TIME = "working time";
    private static final String WORKING_TIME_ID = "working time id";
    private static final String WORKING_DAY_ID = "working day id";

    //orders
    private static final String ORDERS = "orders";
    private static final String IS_CANCELED = "is canceled";
    private static final String MESSAGE_ID = "message id";
    private static final String DIALOG_ID = "dialog id";

    //reviews
    private static final String REVIEWS = "reviews";
    private static final String REVIEW = "review";
    private static final String RATING = "rating";
    private static final String TYPE = "type";
    //messages
    private static final String MESSAGES = "messages";
    private static final String MESSAGE_TIME = "message time";

    private static final String PHOTOS = "photos";
    private static final String PHOTO_LINK = "photo link";
    private static final String OWNER_ID = "owner id";
    private static final String SERVICE_PHOTO = "service photo";

    private static final int PICK_IMAGE_REQUEST = 71;

    private ArrayList<String> phLinToDelete;
    private ArrayList<Uri> fPathToAdd;

    private String serviceId;

    private EditText nameServiceInput;
    private EditText costServiceInput;
    private EditText descriptionServiceInput;
    private DBHelper dbHelper;

    private FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_service);

        Button editServicesBtn = findViewById(R.id.editServiceEditServiceBtn);

        nameServiceInput = findViewById(R.id.nameEditServiceInput);
        costServiceInput = findViewById(R.id.costEditServiceInput);
        descriptionServiceInput = findViewById(R.id.descriptionEditServiceInput);

        manager = getSupportFragmentManager();

        ImageView serviceImage = findViewById(R.id.servicePhotoEditServiceImage);

        FragmentManager manager = getSupportFragmentManager();
        PanelBuilder panelBuilder = new PanelBuilder(this);
        panelBuilder.buildFooter(manager, R.id.footerEditServiceLayout);
        panelBuilder.buildHeader(manager, "Настройки", R.id.headerEditServiceLayout);

        // Получаем id сервиса
        serviceId = getIntent().getStringExtra(SERVICE_ID);

        dbHelper = new DBHelper(this);
        phLinToDelete = new ArrayList<>();
        fPathToAdd = new ArrayList<>();

        //подгружаем фото
        setPhotoFeed(serviceId);

        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String sqlQuery = "SELECT "
                + DBHelper.KEY_NAME_SERVICES + ", "
                + DBHelper.KEY_DESCRIPTION_SERVICES + ", "
                + DBHelper.KEY_MIN_COST_SERVICES
                + " FROM " + DBHelper.TABLE_CONTACTS_SERVICES
                + " WHERE " + DBHelper.KEY_ID + " = ?";
        Cursor cursor = database.rawQuery(sqlQuery, new String[]{serviceId});

        if (cursor.moveToFirst()) {
            int indexName = cursor.getColumnIndex(DBHelper.KEY_NAME_SERVICES);
            int indexCost = cursor.getColumnIndex(DBHelper.KEY_MIN_COST_SERVICES);
            int indexDescription = cursor.getColumnIndex(DBHelper.KEY_DESCRIPTION_SERVICES);

            nameServiceInput.setText(cursor.getString(indexName));
            costServiceInput.setText(cursor.getString(indexCost));
            descriptionServiceInput.setText(cursor.getString(indexDescription));
        }

        cursor.close();
        editServicesBtn.setOnClickListener(this);
        serviceImage.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.editServiceEditServiceBtn:
                String cost = costServiceInput.getText().toString();
                String description = descriptionServiceInput.getText().toString();

                // Создание сервса и заполнение информации о нём
                Service service = new Service();
                service.setId(serviceId);

                if (!service.setName(nameServiceInput.getText().toString())) {
                    Toast.makeText(
                            this,
                            "Имя сервиса должно содержать только буквы",
                            Toast.LENGTH_SHORT).show();
                    break;
                }

                if (cost.length() != 0) {
                    service.setCost(cost);
                }

                if (description.length() != 0) {
                    service.setDescription(description);
                }

                editServiceInLocalStorage(service);
                editServiceInFireBase(service);
                break;

            case R.id.servicePhotoEditServiceImage:
                chooseImage();
                break;

            default:
                break;
        }
    }

    private void chooseImage() {

        //Вызываем стандартную галерею для выбора изображения с помощью Intent.ACTION_PICK:
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        //Тип получаемых объектов - image:
        photoPickerIntent.setType("image/*");
        //Запускаем переход с ожиданием обратного результата в виде информации об изображении:
        startActivityForResult(photoPickerIntent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            Uri filePath = data.getData();
            try {
                //установка картинки на activity
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                addToScreenNewPhoto(bitmap,filePath);

                //serviceImage.setImageBitmap(bitmap);
                //загрузка картинки в fireStorage
                fPathToAdd.add(filePath);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    private void uploadImage(Uri filePath, final String serviceId) {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(PHOTOS);

        if(filePath != null)
        {
            final String photoId = myRef.push().getKey();
            final StorageReference storageReference = firebaseStorage.getReference(SERVICE_PHOTO + "/" + photoId);
            storageReference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            uploadPhotos(uri.toString(),serviceId,photoId);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        }
    }

    private void uploadPhotos(String storageReference, String serviceId,String photoId) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(PHOTOS).child(photoId);

        Map<String,Object> items = new HashMap<>();
        items.put(PHOTO_LINK,storageReference);
        items.put(OWNER_ID,serviceId);

        myRef.updateChildren(items);

        Photo photo = new Photo();
        photo.setPhotoId(photoId);
        photo.setPhotoLink(storageReference);
        photo.setPhotoOwnerId(serviceId);

        addPhotoInLocalStorage(photo);
    }

    private void addPhotoInLocalStorage(Photo photo) {

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(DBHelper.KEY_ID, photo.getPhotoId());
        contentValues.put(DBHelper.KEY_PHOTO_LINK_PHOTOS, photo.getPhotoLink());
        contentValues.put(DBHelper.KEY_OWNER_ID_PHOTOS,photo.getPhotoOwnerId());

        database.insert(DBHelper.TABLE_PHOTOS,null,contentValues);

        goToService();
    }


    private void setPhotoFeed(String serviceId) {

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        //получаем имя, фамилию и город пользователя по его id
        String sqlQuery =
                "SELECT "
                        + DBHelper.KEY_PHOTO_LINK_PHOTOS
                        + " FROM "
                        + DBHelper.TABLE_PHOTOS
                        + " WHERE "
                        + DBHelper.KEY_OWNER_ID_PHOTOS + " = ?";
        Cursor cursor = database.rawQuery(sqlQuery,new String[] {serviceId});

        if(cursor.moveToFirst()){
            do {
                int indexPhotoLink = cursor.getColumnIndex(DBHelper.KEY_PHOTO_LINK_PHOTOS);

                String photoLink = cursor.getString(indexPhotoLink);
                addToScreen(photoLink);
            }while (cursor.moveToNext());
        }
        cursor.close();

    }

    private void addToScreen(String photoLink){
        FragmentTransaction transaction = manager.beginTransaction();
        ServicePhotoElement servicePhotoElement = new ServicePhotoElement(photoLink);
        transaction.add(R.id.feedEditServiceLayout, servicePhotoElement);
        transaction.commit();
    }

    private void addToScreenNewPhoto(Bitmap bitmap, Uri filePath){
        FragmentTransaction transaction = manager.beginTransaction();
        ServicePhotoElement servicePhotoElement = new ServicePhotoElement(bitmap,filePath,"");
        transaction.add(R.id.feedEditServiceLayout, servicePhotoElement);
        transaction.commit();
    }

    private void deletePhotoFromDatabase(final String photoLink) {

        Query photoQuery = FirebaseDatabase.getInstance().getReference(PHOTOS)
                .orderByChild(PHOTO_LINK)
                .equalTo(photoLink);

        photoQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot photosSnapshot) {

                for(DataSnapshot photo: photosSnapshot.getChildren()){
                    String photoId = photo.getKey();

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference(PHOTOS).child(photoId);

                    Map<String,Object> items = new HashMap<>();
                    items.put(PHOTO_LINK,null);
                    items.put(OWNER_ID,null);
                    myRef.updateChildren(items);

                    deletePhotoFromStorage(photoId);
                    deletePhotoFromLocalStorage(photoId);
                }

                for(Uri path: fPathToAdd){
                    uploadImage(path, serviceId);
                }

                if(fPathToAdd.isEmpty()){
                    goToService();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void deletePhotoFromLocalStorage(String photoId) {

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        database.delete(
                DBHelper.TABLE_PHOTOS,
                DBHelper.KEY_ID + " = ?",
                new String[]{photoId});
    }

    private void deletePhotoFromStorage(String photoId) {

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

        final StorageReference storageReference = firebaseStorage.getReference(SERVICE_PHOTO
                + "/"
                + photoId);

        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public void deleteFragment(ServicePhotoElement fr, Uri filePath){
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.remove(fr);
        transaction.commit();
        fPathToAdd.remove(filePath);
    }

    public void deleteFragment(ServicePhotoElement fr, String photoLink){
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.remove(fr);
        transaction.commit();
        phLinToDelete.add(photoLink);
    }

    public void deleteThisService() {
        if (withoutOrders()) {
            confirm(this);
        } else {
            attentionItHasOrders();
        }
    }

    private boolean withoutOrders() {
        //если есть записи на этот сервис, мы не даем его удалить
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String sqlQuery =
                "SELECT "
                        + DBHelper.TABLE_WORKING_TIME +"."+ DBHelper.KEY_USER_ID
                        + " FROM "
                        + DBHelper.TABLE_CONTACTS_SERVICES + ", "
                        + DBHelper.TABLE_WORKING_DAYS + ", "
                        + DBHelper.TABLE_WORKING_TIME
                        + " WHERE "
                        + DBHelper.TABLE_CONTACTS_SERVICES + "."+DBHelper.KEY_ID + " = ? "
                        + " AND "
                        + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME
                        + " = "
                        + DBHelper.TABLE_WORKING_DAYS + "." + DBHelper.KEY_ID
                        + " AND "
                        + DBHelper.KEY_SERVICE_ID_WORKING_DAYS
                        + " = "
                        + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_ID
                        + " AND "
                        + " (STRFTIME('%s', 'now')+3*60*60)-(STRFTIME('%s', " + DBHelper.KEY_DATE_WORKING_DAYS
                        + "||' '||" + DBHelper.KEY_TIME_WORKING_TIME
                        + ")) <= 0";

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{serviceId});

        if(cursor.moveToFirst()){
            int indexUserId = cursor.getColumnIndex(DBHelper.KEY_USER_ID);
            do{
                Log.d(TAG, "withoutOrders: ");
                String userId = cursor.getString(indexUserId);
                if(!userId.equals("0")){
                    return false;
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        return true;
    }

    public void confirm(Context context) {
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setTitle("Удалить услугу");
        dialog.setMessage("Удалить услугу?");
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Да", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int buttonId) {
                deleteThisServiceFromServices();
            }
        });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Нет", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int buttonId) {
            }
        });
        dialog.setIcon(android.R.drawable.ic_dialog_alert);
        dialog.show();
    }

    private void deleteThisServiceFromEverywhere() {
        deleteThisServiceFromServices();
    }

    private void deleteThisServiceFromServices() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(SERVICES + "/" + serviceId);

        Map<String, Object> items = new HashMap<>();
        items.put(NAME, null);
        items.put(COST, null);
        items.put(DESCRIPTION, null);
        items.put(USER_ID, null);
        reference.updateChildren(items);

        deleteThisServiceDayFromWorkingDays();
        deleteServiceFromLocalStorage(DBHelper.TABLE_CONTACTS_SERVICES,serviceId);
    }

    private void deleteThisServiceDayFromWorkingDays() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        final Query query = database.getReference(WORKING_DAYS)
                .orderByChild(SERVICE_ID)
                .equalTo(serviceId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot workingDaysSnapshot) {

                for(DataSnapshot day : workingDaysSnapshot.getChildren()){
                    //удаление из times
                    deleteThisServiceTimesFromWorkingTime(day.getKey());

                    DatabaseReference reference = FirebaseDatabase
                            .getInstance()
                            .getReference(WORKING_DAYS + "/" + day.getKey());

                    Map<String, Object> items = new HashMap<>();
                    items.put(DATA, null);
                    items.put(SERVICE_ID, null);
                    reference.updateChildren(items);

                    deleteServiceFromLocalStorage(DBHelper.TABLE_WORKING_DAYS,day.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void deleteThisServiceTimesFromWorkingTime(String dayId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        final Query query = database.getReference(WORKING_TIME)
                .orderByChild(WORKING_DAY_ID)
                .equalTo(dayId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot workingTimesSnapshot) {

                for(DataSnapshot time : workingTimesSnapshot.getChildren()){
                    //удаление из orders & reviews
                    deleteThisServiceOrdersFromOrders(time.getKey());
                    deleteThisServiceReviewsFromReviews(time.getKey());

                    DatabaseReference reference = FirebaseDatabase
                            .getInstance()
                            .getReference(WORKING_TIME + "/" + time.getKey());

                    Map<String, Object> items = new HashMap<>();
                    items.put(WORKING_DAY_ID, null);
                    items.put(USER_ID, null);
                    items.put(TIME, null);
                    reference.updateChildren(items);
                    deleteServiceFromLocalStorage(DBHelper.TABLE_WORKING_TIME,time.getKey());

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void deleteThisServiceOrdersFromOrders(String timeId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        final Query query = database.getReference(ORDERS)
                .orderByChild(WORKING_TIME_ID)
                .equalTo(timeId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ordersSnapshot) {

                for(DataSnapshot order : ordersSnapshot.getChildren()){
                    //удалять сообщения
                    deleteThisServiceMessagesFromMessages(String.valueOf(order.child(MESSAGE_ID).getValue()));
                    DatabaseReference reference = FirebaseDatabase
                            .getInstance()
                            .getReference(ORDERS + "/" + order.getKey());

                    Map<String, Object> items = new HashMap<>();
                    items.put(IS_CANCELED, null);
                    items.put(MESSAGE_ID, null);
                    items.put(WORKING_TIME_ID, null);
                    reference.updateChildren(items);
                    deleteServiceFromLocalStorage(DBHelper.TABLE_ORDERS,order.getKey());

                }
                //в конце перезаходим
                goToAuthorization();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

    }

    private void deleteThisServiceReviewsFromReviews(String timeId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        final Query query = database.getReference(REVIEWS)
                .orderByChild(WORKING_TIME_ID)
                .equalTo(timeId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot reviewsSnapshot) {

                for(DataSnapshot review : reviewsSnapshot.getChildren()){

                    deleteThisServiceMessagesFromMessages(String.valueOf(review.child(MESSAGE_ID).getValue()));

                    DatabaseReference reference = FirebaseDatabase
                            .getInstance()
                            .getReference(REVIEWS + "/" + review.getKey());

                    Map<String, Object> items = new HashMap<>();
                    items.put(RATING, null);
                    items.put(MESSAGE_ID, null);
                    items.put(WORKING_TIME_ID, null);
                    items.put(REVIEW, null);
                    items.put(TYPE, null);
                    reference.updateChildren(items);

                    deleteServiceFromLocalStorage(DBHelper.TABLE_REVIEWS, review.getKey());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void deleteThisServiceMessagesFromMessages(String messageId) {

        DatabaseReference reference = FirebaseDatabase
                .getInstance()
                .getReference(MESSAGES + "/" + messageId);

        Map<String, Object> items = new HashMap<>();
        items.put(MESSAGE_TIME, null);
        items.put(DIALOG_ID, null);
        reference.updateChildren(items);

        deleteServiceFromLocalStorage(DBHelper.TABLE_MESSAGES,messageId);
    }

    private void deleteServiceFromLocalStorage(String tableName, String id) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.delete(tableName,
                DBHelper.KEY_ID + " = ?",
                new String[]{id});
    }

    private void editServiceInLocalStorage(Service service) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        if (service.getName() != null)
            contentValues.put(DBHelper.KEY_NAME_SERVICES, service.getName());
        if (service.getCost() != null)
            contentValues.put(DBHelper.KEY_MIN_COST_SERVICES, service.getCost());
        if (service.getDescription() != null)
            contentValues.put(DBHelper.KEY_DESCRIPTION_SERVICES, service.getDescription());
        if (contentValues.size() > 0) {
            database.update(DBHelper.TABLE_CONTACTS_SERVICES, contentValues,
                    DBHelper.KEY_ID + " = ?",
                    new String[]{service.getId()});
        }
    }

    private void editServiceInFireBase(Service service) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(SERVICES + "/" + service.getId());

        Map<String, Object> items = new HashMap<>();
        if (service.getName() != null) items.put(NAME, service.getName());
        if (service.getCost() != null) items.put(COST, service.getCost());
        if (service.getDescription() != null) items.put(DESCRIPTION, service.getDescription());
        reference.updateChildren(items);

        for(String photoLink: phLinToDelete) {
            deletePhotoFromDatabase(photoLink);
        }

        if(phLinToDelete.isEmpty()){
            //добавление новых картинок при редактировании
            for(Uri path: fPathToAdd){
                uploadImage(path, serviceId);
            }
            if(fPathToAdd.isEmpty()){
                goToService();
            }
        }
    }

    private void attentionItHasOrders() {
        Toast.makeText(this,"Нельзя удалить сервис, на который есть записи",
                Toast.LENGTH_SHORT).show();
    }
    private void goToService() {
        super.onBackPressed();
        finish();
    }

    private void goToAuthorization(){
        Intent intent = new Intent(EditService.this, Authorization.class);
        startActivity(intent);
        finish();
    }


}
