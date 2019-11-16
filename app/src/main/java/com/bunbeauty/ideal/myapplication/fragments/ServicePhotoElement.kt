package com.bunbeauty.ideal.myapplication.fragments

import android.graphics.Bitmap
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

class ServicePhotoElement(private val bitmap: Bitmap, private val filePath: String, private val status: String) : Fragment(), View.OnClickListener {
    private lateinit var cancelPhoto: Button
    private lateinit var photo: ImageView

    private lateinit var photoLink: String

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
                    (this.activity as AddingServiceActivity).removePhoto(this, filePath)
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
