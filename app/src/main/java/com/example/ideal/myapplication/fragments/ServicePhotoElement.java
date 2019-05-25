package com.example.ideal.myapplication.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.createService.AddService;
import com.example.ideal.myapplication.editing.EditService;
import com.squareup.picasso.Picasso;

public class ServicePhotoElement extends Fragment implements View.OnClickListener {

    private static final String ADD_SERVICE = "add service";
    private Button cancelPhoto;
    private ImageView photo;

    private Bitmap bitmap;
    private String photoLink;
    private String status;
    private Uri filePath;
    private final String TAG = "DBInf";

    public ServicePhotoElement() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public ServicePhotoElement(Bitmap _bitmap, Uri _filePath, String _status) {
        bitmap = _bitmap;
        filePath = _filePath;
        status = _status;
    }

    @SuppressLint("ValidFragment")
    public ServicePhotoElement(String _photoLink) {
        photoLink = _photoLink;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        int width = getResources().getDimensionPixelSize(R.dimen.photo_width);
        int height = getResources().getDimensionPixelSize(R.dimen.photo_height);

        cancelPhoto = view.findViewById(R.id.cancelServicePhotoElement);
        photo = view.findViewById(R.id.imageServicePhotoElementImage);
        cancelPhoto.setOnClickListener(this);

        LinearLayout layout = view.findViewById(R.id.servicePhotoElementLayout);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layout.getLayoutParams();
        params.setMargins(0, 0, 25, 0);
        layout.setLayoutParams(params);

        //если из addService
        if (bitmap != null) {
            photo.setImageBitmap(bitmap);
            photo.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Log.d(TAG, "onViewCreated: ");
        }

        //если из editService
        if (photoLink != null) {
            //установка аватарки
            Picasso.get()
                    .load(photoLink)
                    .resize(width, height)
                    .centerCrop()
                    .into(photo);
        }
    }

    @Override
    public void onClick(View v) {
        if (bitmap != null) {
            //удаление фрагмента
            //если мы на addService
            if (status.equals(ADD_SERVICE)) {
                AddService activity = (AddService) this.getActivity();
                if (activity != null) {
                    activity.deleteFragment(this, filePath);
                }
            } else {
                //на edit service
                EditService editServiceActivity = (EditService) this.getActivity();
                if (editServiceActivity != null) {
                    editServiceActivity.deleteFragment(this, filePath);
                }
            }
        } else {
            // только на ES можно удлаять, уже загруженные
            EditService activity = (EditService) this.getActivity();
            if (activity != null)
                activity.deleteFragment(this, photoLink);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.service_photo_element, null);
    }
}
