package com.example.ideal.myapplication.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.createService.AddService;
import com.example.ideal.myapplication.editing.EditService;
import com.squareup.picasso.Picasso;

public class ServicePhotoElement extends Fragment implements View.OnClickListener {

    private Button cancelPhoto;
    private ImageView photo;

    private Bitmap bitmap;
    private String photoLink;
    private Uri filePath;
    private final String TAG = "DBInf";

    public ServicePhotoElement() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public ServicePhotoElement(Bitmap _bitmap, Uri _filePath) {
        bitmap = _bitmap;
        filePath = _filePath;
    }

    @SuppressLint("ValidFragment")
    public ServicePhotoElement(String _photoLink) {
        photoLink = _photoLink;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        cancelPhoto = view.findViewById(R.id.cancelServicePhotoElement);
        photo = view.findViewById(R.id.imageServicePhotoElementImage);
        cancelPhoto.setOnClickListener(this);

        //если из addService
        if(bitmap!=null) {
            photo.setImageBitmap(bitmap);
        }

        //если из editService
        if(photoLink!=null){
            //установка аватарки
            Picasso.get()
                    .load(photoLink)
                    .into(photo);
        }
    }

    @Override
    public void onClick(View v) {
        if(bitmap!=null) {
            //удаление фрагмента
            AddService activity = (AddService) this.getActivity();
            if (activity != null)
                activity.deleteFragment(this, filePath);
        }
        else {
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
