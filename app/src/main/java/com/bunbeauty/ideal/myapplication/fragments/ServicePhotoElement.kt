package com.bunbeauty.ideal.myapplication.fragments

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.createService.AddingServiceActivity
import com.bunbeauty.ideal.myapplication.editing.EditService
import com.squareup.picasso.Picasso

class ServicePhotoElement : Fragment, View.OnClickListener {
    private lateinit var cancelPhoto: Button
    private lateinit var photo: ImageView

    private lateinit var bitmap: Bitmap
    private lateinit var photoLink: String
    private lateinit var status: String
    private lateinit var filePath: Uri

    constructor() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    constructor(_bitmap: Bitmap, _filePath: Uri, _status: String) {
        bitmap = _bitmap
        filePath = _filePath
        status = _status
    }

    @SuppressLint("ValidFragment")
    constructor(_photoLink: String) {
        photoLink = _photoLink
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val width = resources.getDimensionPixelSize(R.dimen.photo_width)
        val height = resources.getDimensionPixelSize(R.dimen.photo_height)

        cancelPhoto = view.findViewById(R.id.cancelServicePhotoElement)
        photo = view.findViewById(R.id.imageServicePhotoElementImage)
        cancelPhoto.setOnClickListener(this)

        val layout = view.findViewById<LinearLayout>(R.id.servicePhotoElementLayout)

        val params = layout.layoutParams as LinearLayout.LayoutParams
        params.setMargins(0, 0, 25, 0)
        layout.layoutParams = params

        //если из addService
        if (status == ADD_SERVICE) {
            photo.setImageBitmap(bitmap)
            photo.scaleType = ImageView.ScaleType.CENTER_CROP
        } else {
            //установка аватарки
            Picasso.get()
                    .load(photoLink)
                    .resize(width, height)
                    .centerCrop()
                    .into(photo)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.cancelServicePhotoElement -> {
                //удаление фрагмента
                //если мы на addService
                if (status == ADD_SERVICE) {
                    (this.activity as AddingServiceActivity).deleteFragment(this, filePath)
                } else {
                    (this.activity as EditService).deleteFragment(this, filePath)
                }
            }

            R.id.imageServicePhotoElementImage -> {
                print("KEK")
                // развертывание на всю активити
            }

        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.service_photo_element, null)
    }

    companion object {

        const val ADD_SERVICE = "add service"
    }
}
