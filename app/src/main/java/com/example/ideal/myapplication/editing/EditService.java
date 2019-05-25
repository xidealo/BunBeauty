package com.example.ideal.myapplication.editing;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class EditService extends AppCompatActivity implements View.OnClickListener {

    private static final String SERVICE_ID = "service id";
    private final String TAG = "DBInf";
    private static final String USERS = "users";

    private static final String SERVICES = "services";
    private static final String NAME = "name";
    private static final String COST = "cost";
    private static final String DESCRIPTION = "description";
    private static final String PHOTOS = "photos";

    private static final String PHOTO_LINK = "photo link";
    private static final String SERVICE_PHOTO = "service photo";
    private static final String CATEGORY = "category";
    private static final String ADDRESS = "address";

    private static final int PICK_IMAGE_REQUEST = 71;

    private ArrayList<String> phLinToDelete;
    private ArrayList<Uri> fPathToAdd;

    private String serviceId;

    private EditText nameServiceInput;
    private EditText costServiceInput;
    private EditText descriptionServiceInput;
    private EditText addressServiceInput;
    private DBHelper dbHelper;
    private Spinner categorySpinner;
    private FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_service);

        Button editServicesBtn = findViewById(R.id.editServiceEditServiceBtn);

        nameServiceInput = findViewById(R.id.nameEditServiceInput);
        costServiceInput = findViewById(R.id.costEditServiceInput);
        descriptionServiceInput = findViewById(R.id.descriptionEditServiceInput);
        addressServiceInput = findViewById(R.id.addressEditServiceInput);
        categorySpinner = findViewById(R.id.categoryEditServiceSpinner);

        manager = getSupportFragmentManager();

        ImageView serviceImage = findViewById(R.id.servicePhotoEditServiceImage);

        // Получаем id сервиса
        serviceId = getIntent().getStringExtra(SERVICE_ID);

        dbHelper = new DBHelper(this);
        phLinToDelete = new ArrayList<>();
        fPathToAdd = new ArrayList<>();

        //подгружаем фото
        setPhotoFeed(serviceId);

        createEditServiceScreen();

        editServicesBtn.setOnClickListener(this);
        serviceImage.setOnClickListener(this);
    }

    private void createEditServiceScreen() {
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        String sqlQuery = "SELECT *"
                + " FROM " + DBHelper.TABLE_CONTACTS_SERVICES
                + " WHERE " + DBHelper.KEY_ID + " = ?";
        Cursor cursor = database.rawQuery(sqlQuery, new String[]{serviceId});

        if (cursor.moveToFirst()) {
            int indexName = cursor.getColumnIndex(DBHelper.KEY_NAME_SERVICES);
            int indexCost = cursor.getColumnIndex(DBHelper.KEY_MIN_COST_SERVICES);
            int indexDescription = cursor.getColumnIndex(DBHelper.KEY_DESCRIPTION_SERVICES);
            int indexAddress = cursor.getColumnIndex(DBHelper.KEY_ADDRESS_SERVICES);
            int indexCategory = cursor.getColumnIndex(DBHelper.KEY_CATEGORY_SERVICES);

            nameServiceInput.setText(cursor.getString(indexName));
            costServiceInput.setText(cursor.getString(indexCost));
            descriptionServiceInput.setText(cursor.getString(indexDescription));
            addressServiceInput.setText(cursor.getString(indexAddress));
            String category = cursor.getString(indexCategory);

            Resources res = getResources();
            String[] categories = res.getStringArray(R.array.categoryForEditService);
            categorySpinner.setSelection(Arrays.asList(categories).indexOf(category));

            cursor.close();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.editServiceEditServiceBtn:
                String description = descriptionServiceInput.getText().toString();

                // Создание сервса и заполнение информации о нём
                Service service = new Service();
                service.setId(serviceId);
                service.setUserId(getUserId());
                if (!service.setName(nameServiceInput.getText().toString())) {
                    Toast.makeText(
                            this,
                            "Имя сервиса должно содержать только буквы",
                            Toast.LENGTH_SHORT).show();
                    break;
                }

                if (!service.setCost(costServiceInput.getText().toString())) {
                    Toast.makeText(
                            this,
                            "Цена не может содержать больше 8 символов",
                            Toast.LENGTH_SHORT).show();
                    break;
                }

                if (description.length() != 0) {
                    service.setDescription(description);
                }
                //если содержить выбрать категорию, значит не меняем категорию
                service.setCategory(categorySpinner.getSelectedItem().toString().toLowerCase());

                String address = addressServiceInput.getText().toString();
                if (address.isEmpty()) {
                    Toast.makeText(
                            this,
                            "Не указан адрес",
                            Toast.LENGTH_SHORT).show();
                    break;
                }
                service.setAddress(address);
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
    protected void onResume() {
        super.onResume();

        FragmentManager manager = getSupportFragmentManager();
        PanelBuilder panelBuilder = new PanelBuilder();
        panelBuilder.buildFooter(manager, R.id.footerEditServiceLayout);
        panelBuilder.buildHeader(manager, "Настройки", R.id.headerEditServiceLayout);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //установка картинки на activity
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                addToScreenNewPhoto(bitmap, filePath);

                //serviceImage.setImageBitmap(bitmap);
                //загрузка картинки в fireStorage
                fPathToAdd.add(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage(Uri filePath, final String serviceId) {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(PHOTOS);

        if (filePath != null) {
            final String photoId = myRef.push().getKey();
            final StorageReference storageReference = firebaseStorage.getReference(SERVICE_PHOTO + "/" + photoId);
            storageReference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            uploadPhotos(uri.toString(), serviceId, photoId);
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

    private void uploadPhotos(String storageReference, String serviceId, String photoId) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database
                .getReference(USERS)
                .child(getUserId())
                .child(SERVICES)
                .child(serviceId)
                .child(PHOTOS)
                .child(photoId);

        Map<String, Object> items = new HashMap<>();
        items.put(PHOTO_LINK, storageReference);

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
        contentValues.put(DBHelper.KEY_OWNER_ID_PHOTOS, photo.getPhotoOwnerId());

        database.insert(DBHelper.TABLE_PHOTOS, null, contentValues);

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
        Cursor cursor = database.rawQuery(sqlQuery, new String[]{serviceId});

        if (cursor.moveToFirst()) {
            do {
                int indexPhotoLink = cursor.getColumnIndex(DBHelper.KEY_PHOTO_LINK_PHOTOS);

                String photoLink = cursor.getString(indexPhotoLink);
                addToScreen(photoLink);
            } while (cursor.moveToNext());
        }
        cursor.close();

    }

    private void addToScreen(String photoLink) {
        FragmentTransaction transaction = manager.beginTransaction();
        ServicePhotoElement servicePhotoElement = new ServicePhotoElement(photoLink);
        transaction.add(R.id.feedEditServiceLayout, servicePhotoElement);
        transaction.commit();
    }

    private void addToScreenNewPhoto(Bitmap bitmap, Uri filePath) {
        FragmentTransaction transaction = manager.beginTransaction();
        ServicePhotoElement servicePhotoElement = new ServicePhotoElement(bitmap, filePath, "");
        transaction.add(R.id.feedEditServiceLayout, servicePhotoElement);
        transaction.commit();
    }

    private void deletePhotoFromDatabase(final String photoLink) {

        Query photoQuery = FirebaseDatabase.getInstance()
                .getReference(USERS)
                .child(getUserId())
                .child(SERVICES)
                .child(serviceId)
                .child(PHOTOS)
                .orderByChild(PHOTO_LINK)
                .equalTo(photoLink);

        photoQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot photosSnapshot) {

                for (DataSnapshot photo : photosSnapshot.getChildren()) {
                    String photoId = photo.getKey();

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database
                            .getReference(USERS)
                            .child(getUserId())
                            .child(SERVICES)
                            .child(serviceId)
                            .child(PHOTOS);

                    myRef.child(photoId).removeValue();

                    deletePhotoFromStorage(photoId);
                    deletePhotoFromLocalStorage(photoId);
                }

                for (Uri path : fPathToAdd) {
                    uploadImage(path, serviceId);
                }

                if (fPathToAdd.isEmpty()) {
                    goToService();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
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
        final StorageReference storageReference = firebaseStorage.getReference(SERVICE_PHOTO + "/" + photoId);

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

    public void deleteFragment(ServicePhotoElement fr, Uri filePath) {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.remove(fr);
        transaction.commit();
        fPathToAdd.remove(filePath);
    }

    public void deleteFragment(ServicePhotoElement fr, String photoLink) {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.remove(fr);
        transaction.commit();
        phLinToDelete.add(photoLink);
    }

    public void deleteThisService() {
        //Сложно удалить из-за зависимостей, позже можно доработать
        /*if (withoutOrders()) {
            confirm(this);
        } else {
            attentionItHasOrders();
        }*/
    }

    private boolean withoutOrders() {
        //если есть записи на этот сервис, мы не даем его удалить
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String sqlQuery =
                "SELECT *"
                        + " FROM "
                        + DBHelper.TABLE_WORKING_DAYS + ", "
                        + DBHelper.TABLE_ORDERS + ", "
                        + DBHelper.TABLE_WORKING_TIME
                        + " WHERE "
                        + DBHelper.KEY_SERVICE_ID_WORKING_DAYS + " = ? "
                        + " AND "
                        + DBHelper.KEY_WORKING_TIME_ID_ORDERS
                        + " = "
                        + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID
                        + " AND "
                        + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME
                        + " = "
                        + DBHelper.TABLE_WORKING_DAYS + "." + DBHelper.KEY_ID
                        + " AND "
                        + " (STRFTIME('%s', 'now')+3*60*60)-(STRFTIME('%s', " + DBHelper.KEY_DATE_WORKING_DAYS
                        + "||' '||" + DBHelper.KEY_TIME_WORKING_TIME
                        + ")) <= 0";

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{serviceId});

        if (cursor.moveToFirst()) {
            return false;
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


    private void deleteThisServiceFromServices() {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference(USERS)
                .child(getUserId())
                .child(SERVICES);

        reference.child(serviceId).removeValue();
        deleteServiceFromLocalStorage(DBHelper.TABLE_CONTACTS_SERVICES, serviceId);
    }

    private void deleteServiceFromLocalStorage(String tableName, String id) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.delete(tableName,
                DBHelper.KEY_ID + " = ?",
                new String[]{id});
    }

    private void editServiceInFireBase(Service service) {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference(USERS)
                .child(service.getUserId())
                .child(SERVICES)
                .child(service.getId());

        Map<String, Object> items = new HashMap<>();
        items.put(NAME, service.getName());
        items.put(COST, service.getCost());
        items.put(DESCRIPTION, service.getDescription());
        items.put(ADDRESS, service.getAddress());
        items.put(CATEGORY, service.getCategory());
        reference.updateChildren(items);
        editServiceInLocalStorage(service);

        for (String photoLink : phLinToDelete) {
            deletePhotoFromDatabase(photoLink);
        }

        if (phLinToDelete.isEmpty()) {
            //добавление новых картинок при редактировании
            for (Uri path : fPathToAdd) {
                uploadImage(path, serviceId);
            }
            if (fPathToAdd.isEmpty()) {
                goToService();
            }
        }
    }

    private void editServiceInLocalStorage(Service service) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DBHelper.KEY_NAME_SERVICES, service.getName());
        contentValues.put(DBHelper.KEY_MIN_COST_SERVICES, service.getCost());
        contentValues.put(DBHelper.KEY_DESCRIPTION_SERVICES, service.getDescription());
        contentValues.put(DBHelper.KEY_ADDRESS_SERVICES, service.getAddress());
        contentValues.put(DBHelper.KEY_CATEGORY_SERVICES, service.getCategory());
        database.update(DBHelper.TABLE_CONTACTS_SERVICES, contentValues,
                DBHelper.KEY_ID + " = ?",
                new String[]{service.getId()});

    }

    private void attentionItHasOrders() {
        Toast.makeText(this, "Нельзя удалить сервис, на который есть записи",
                Toast.LENGTH_SHORT).show();
    }

    private void goToService() {
        super.onBackPressed();
        finish();
    }

    private String getUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
