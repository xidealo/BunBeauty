package com.example.ideal.myapplication.createService;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.fragments.ServicePhotoElement;
import com.example.ideal.myapplication.fragments.objects.Photo;
import com.example.ideal.myapplication.fragments.objects.Service;
import com.example.ideal.myapplication.helpApi.PanelBuilder;
import com.example.ideal.myapplication.other.DBHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddService extends AppCompatActivity implements View.OnClickListener {

    private static final String SERVICE_ID = "service id";
    private static final String STATUS_USER_BY_SERVICE = "status User";

    private static final String SERVICES = "services";
    private static final String NAME = "name";
    private static final String COST = "cost";
    private static final String DESCRIPTION = "description";
    private static final String USER_ID = "user id";
    private static final String IS_PREMIUM = "is premium";

    private static final int PICK_IMAGE_REQUEST = 71;
    private static final String SERVICE_PHOTO = "service photo";
    private static final String PHOTOS = "photos";
    private static final String PHOTO_LINK = "photo link";
    private static final String OWNER_ID = "owner id";

    private EditText nameServiceInput;
    private EditText costAddServiceInput;
    private EditText descriptionServiceInput;
    //храним ссылки на картинки в хранилище
    private ArrayList<Uri> fpath;

    private FragmentManager manager;

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_service);

        Button addServicesBtn = findViewById(R.id.addServiceAddServiceBtn);

        nameServiceInput = findViewById(R.id.nameAddServiceInput);
        costAddServiceInput = findViewById(R.id.costAddServiceInput);
        descriptionServiceInput = findViewById(R.id.descriptionAddServiceInput);
        ImageView serviceImage = findViewById(R.id.servicePhotoAddServiceImage);

        manager = getSupportFragmentManager();
        PanelBuilder panelBuilder = new PanelBuilder();
        panelBuilder.buildFooter(manager, R.id.footerAddServiceLayout);
        panelBuilder.buildHeader(manager, "Создание сервиса", R.id.headerAddServiceLayout);

        dbHelper = new DBHelper(this);
        fpath = new ArrayList<>();

        addServicesBtn.setOnClickListener(this);
        serviceImage.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.addServiceAddServiceBtn:
                if(isFullInputs()) {
                    Service service = new Service();
                    if (!service.setName(nameServiceInput.getText().toString())) {
                        Toast.makeText(
                                this,
                                "Имя сервиса должно содержать только буквы",
                                Toast.LENGTH_SHORT).show();
                        break;
                    }

                    service.setDescription(descriptionServiceInput.getText().toString());

                    if(!service.setCost(costAddServiceInput.getText().toString())){
                        Toast.makeText(
                                this,
                                "Цена не может содержать больше 8 символов ",
                                Toast.LENGTH_SHORT).show();
                        break;
                    }

                    service.setIsPremium(false);

                    uploadService(service);
                }
                else {
                    Toast.makeText(this, getString(R.string.empty_field), Toast.LENGTH_SHORT).show();
                }
                break;
                case R.id.servicePhotoAddServiceImage:
                        chooseImage();
                    break;
            default:
                break;
        }
    }

    private void uploadService(Service service) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(SERVICES);
        String userId = getUserId();

        Map<String,Object> items = new HashMap<>();
        items.put(NAME,service.getName().toLowerCase());
        items.put(COST,service.getCost());
        items.put(DESCRIPTION,service.getDescription());
        items.put(USER_ID,userId);
        items.put(IS_PREMIUM,service.getIsPremium());
        String serviceId =  myRef.push().getKey();
        myRef = database.getReference(SERVICES).child(serviceId);
        myRef.updateChildren(items);

        service.setId(serviceId);
        addServiceInLocalStorage(service);

        for(Uri path: fpath){
            uploadImage(path, serviceId);
        }
    }

    private void addServiceInLocalStorage(Service service){

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        String userId = getUserId();
        //добавление в БД
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_ID, service.getId());
        contentValues.put(DBHelper.KEY_NAME_SERVICES, service.getName().toLowerCase());
        contentValues.put(DBHelper.KEY_MIN_COST_SERVICES, service.getCost());
        contentValues.put(DBHelper.KEY_DESCRIPTION_SERVICES, service.getDescription());
        contentValues.put(DBHelper.KEY_USER_ID, userId);

        database.insert(DBHelper.TABLE_CONTACTS_SERVICES,null,contentValues);
        goToMyCalendar(getString(R.string.status_worker),service.getId());

    }

    protected Boolean isFullInputs(){
        if(nameServiceInput.getText().toString().isEmpty()) return false;
        if(descriptionServiceInput.getText().toString().isEmpty()) return false;
        if(costAddServiceInput.getText().toString().isEmpty()) return false;

        return  true;
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

                addToScreen(bitmap,filePath);

                //serviceImage.setImageBitmap(bitmap);
                //загрузка картинки в fireStorage
                fpath.add(filePath);
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
    }

    private void addToScreen(Bitmap bitmap, Uri filePath){
        FragmentTransaction transaction = manager.beginTransaction();
        ServicePhotoElement servicePhotoElement = new ServicePhotoElement(bitmap,filePath,"add service");
        transaction.add(R.id.feedAddServiceLayout, servicePhotoElement);
        transaction.commit();
    }


    public void deleteFragment(ServicePhotoElement fr, Uri filePath){
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.remove(fr);
        transaction.commit();
        fpath.remove(filePath);
    }

    private String getUserId(){
        return FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
    }

    private void goToMyCalendar(String status, String serviceId) {
        Intent intent = new Intent(this, MyCalendar.class);
        intent.putExtra(SERVICE_ID, serviceId);
        intent.putExtra(STATUS_USER_BY_SERVICE, status);

        startActivity(intent);
        finish();
    }
}