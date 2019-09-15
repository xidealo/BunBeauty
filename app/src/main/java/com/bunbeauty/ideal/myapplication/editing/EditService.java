package com.bunbeauty.ideal.myapplication.editing;

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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ideal.myapplication.R;
import com.bunbeauty.ideal.myapplication.fragments.ServicePhotoElement;
import com.bunbeauty.ideal.myapplication.fragments.objects.Photo;
import com.bunbeauty.ideal.myapplication.fragments.objects.Service;
import com.bunbeauty.ideal.myapplication.helpApi.PanelBuilder;
import com.bunbeauty.ideal.myapplication.fragments.CategoryElement;
import com.bunbeauty.ideal.myapplication.other.DBHelper;
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

    private final String TAG = "DBInf";
    private static final String SERVICE_ID = "service id";
    private static final String USERS = "users";

    private static final String SERVICES = "services";
    private static final String NAME = "name";
    private static final String COST = "cost";
    private static final String DESCRIPTION = "description";
    private static final String PHOTOS = "photos";
    private static final String CATEGORY = "category";
    private static final String ADDRESS = "address";
    private static final String TAGS = "tags";

    private static final String PHOTO_LINK = "photo link";
    private static final String SERVICE_PHOTO = "service photo";
    private static final int MAX_COUNT_OF_IMAGES = 3;

    private static final int PICK_IMAGE_REQUEST = 71;

    private ArrayList<String> phLinToDelete;
    private ArrayList<Uri> fPathToAdd;

    private String serviceId;
    private int counterOfUploadImage;

    private EditText nameServiceInput;
    private EditText costServiceInput;
    private EditText descriptionServiceInput;
    private EditText addressServiceInput;
    private ProgressBar progressBar;
    private Button editServicesBtn;
    private DBHelper dbHelper;
    private FragmentManager manager;
    private CategoryElement categoryElement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_service);

        init();
    }

    private void init() {
        editServicesBtn = findViewById(R.id.editServiceEditServiceBtn);
        nameServiceInput = findViewById(R.id.nameEditServiceInput);
        costServiceInput = findViewById(R.id.costEditServiceInput);
        descriptionServiceInput = findViewById(R.id.descriptionEditServiceInput);
        addressServiceInput = findViewById(R.id.addressEditServiceInput);
        progressBar = findViewById(R.id.progressBarEditService);

        manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        categoryElement = new CategoryElement(this);
        transaction.add(R.id.categoryEditServiceLayout, categoryElement);
        transaction.commit();

        manager = getSupportFragmentManager();
        TextView serviceImage = findViewById(R.id.servicePhotoEditServiceImage);
        dbHelper = new DBHelper(this);
        phLinToDelete = new ArrayList<>();
        fPathToAdd = new ArrayList<>();

        // Получаем id сервиса
        serviceId = getIntent().getStringExtra(SERVICE_ID);

        //подгружаем фото
        setPhotoFeed(serviceId);

        //Заполняем поля
        fillFields();

        editServicesBtn.setOnClickListener(this);
        serviceImage.setOnClickListener(this);
    }

    private void fillFields() {
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
            ArrayList<String> categoriesArray = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.choice_categories)));
            categoryElement.setCategory(categoriesArray.indexOf(cursor.getString(indexCategory)));
            setTags(serviceId);

            cursor.close();
        }
    }

    private void setTags(String serviceId) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        String sqlQuery = "SELECT " + DBHelper.KEY_TAG_TAGS
                + " FROM " + DBHelper.TABLE_TAGS
                + " WHERE " + DBHelper.KEY_SERVICE_ID_TAGS + " = ?";
        Cursor cursor = database.rawQuery(sqlQuery, new String[]{serviceId});

        if (cursor.moveToFirst()) {
            int indexTag = cursor.getColumnIndex(DBHelper.KEY_TAG_TAGS);
            do {
                categoryElement.addTag(cursor.getString(indexTag));
            } while (cursor.moveToNext());
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
                if (!service.setName(nameServiceInput.getText().toString().toLowerCase())) {
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
                service.setCategory(categoryElement.getCategory());

                String address = addressServiceInput.getText().toString();
                if (address.isEmpty()) {
                    Toast.makeText(
                            this,
                            "Не указан адрес",
                            Toast.LENGTH_SHORT).show();
                    break;
                }
                service.setAddress(address);

                service.setTags(categoryElement.getTagsArray());
                Log.d(TAG, "onClick: " + fPathToAdd);
                if(fPathToAdd.size() <= MAX_COUNT_OF_IMAGES){
                    editServiceInFireBase(service);
                    editServicesBtn.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                } else{
                    attentionMoreTenImages();
                }
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
        panelBuilder.buildHeader(manager, "Редактирование услуги", R.id.headerEditServiceLayout);
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

    private void uploadImage(Uri filePath, final String serviceId, final int countOfUploadImage) {
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
                            uploadPhotos(uri.toString(), serviceId, photoId, countOfUploadImage);
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

    private void uploadPhotos(String storageReference, String serviceId, String photoId, int countOfUploadImage) {

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

        addPhotoInLocalStorage(photo, countOfUploadImage);
    }

    private void addPhotoInLocalStorage(Photo photo, int countOfUploadImage) {

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(DBHelper.KEY_ID, photo.getPhotoId());
        contentValues.put(DBHelper.KEY_PHOTO_LINK_PHOTOS, photo.getPhotoLink());
        contentValues.put(DBHelper.KEY_OWNER_ID_PHOTOS, photo.getPhotoOwnerId());

        database.insert(DBHelper.TABLE_PHOTOS, null, contentValues);

        counterOfUploadImage++;

        if (counterOfUploadImage == countOfUploadImage)
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
                    uploadImage(path, serviceId, fPathToAdd.size());
                }

                if (fPathToAdd.isEmpty()) {
                    goToService();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                editServicesBtn.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
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
        Map<String, String> tagsMap = new HashMap<>();
        items.put(NAME, service.getName());
        items.put(COST, service.getCost());
        items.put(DESCRIPTION, service.getDescription());
        items.put(ADDRESS, service.getAddress());
        items.put(CATEGORY, service.getCategory());
        for (String tag : service.getTags()) {
            tagsMap.put(String.valueOf(tag.hashCode()), tag);
        }
        items.put(TAGS, tagsMap);
        reference.updateChildren(items);

        editServiceInLocalStorage(service);

        for (String photoLink : phLinToDelete) {
            deletePhotoFromDatabase(photoLink);
        }

        if (phLinToDelete.isEmpty()) {
            //добавление новых картинок при редактировании
            for (Uri path : fPathToAdd) {
                uploadImage(path, serviceId, fPathToAdd.size());
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
                new String[]{serviceId});

        int c = database.delete(DBHelper.TABLE_TAGS,
                DBHelper.KEY_SERVICE_ID_TAGS + " = ?",
                new String[]{serviceId});

        contentValues.clear();
        contentValues.put(DBHelper.KEY_SERVICE_ID_TAGS, serviceId);
        for (String tag : service.getTags()) {

            contentValues.put(DBHelper.KEY_TAG_TAGS, tag);
            database.insert(DBHelper.TABLE_TAGS, null, contentValues);
        }
    }

    private void attentionItHasOrders() {
        Toast.makeText(this, "Нельзя удалить сервис, на который есть записи",
                Toast.LENGTH_SHORT).show();
    }

    private void goToService() {
        super.onBackPressed();
        finish();
    }

    private void attentionMoreTenImages() {
        Toast.makeText(this, "Должно быть меньше 10 фотографий", Toast.LENGTH_SHORT).show();
    }

    private String getUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
