package com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.elements

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
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.IPhotoEditable
import com.squareup.picasso.Picasso

class ServicePhotoFragment : Fragment(), View.OnClickListener {
    private lateinit var removePhotoBtn: Button
    private lateinit var photoImage: ImageView

    private lateinit var bitmap: Bitmap
    private lateinit var filePath: String
    private var photoLink: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            bitmap = it.getParcelable(BITMAP)!!
            filePath = it.getString(FILE_PATH)!!
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        removePhotoBtn = view.findViewById(R.id.cancelServicePhotoElement)
        photoImage = view.findViewById(R.id.imageServicePhotoElementImage)
        removePhotoBtn.setOnClickListener(this)

        val layout = view.findViewById<LinearLayout>(R.id.servicePhotoElementLayout)
        val params = layout.layoutParams as LinearLayout.LayoutParams
        params.setMargins(0, 0, 25, 0)
        layout.layoutParams = params

        //если из addService
        if (photoLink.isEmpty()) {
            photoImage.setImageBitmap(bitmap)
            photoImage.scaleType = ImageView.ScaleType.CENTER_CROP
        } else {
            val width = resources.getDimensionPixelSize(R.dimen.photo_width)
            val height = resources.getDimensionPixelSize(R.dimen.photo_height)

            Picasso.get()
                .load(photoLink)
                .resize(width, height)
                .centerCrop()
                .into(photoImage)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.cancelServicePhotoElement -> {
                //remove photoImage
                (activity as IPhotoEditable).removePhoto(this, filePath)

                /*if (isExist == IS_EXIST) {
                    (this.activity as AddingServiceActivity).removePhoto(this, filePath)
                } else {
                    (this.activity as EditService).deleteFragment(this, filePath)
                }*/
            }

            R.id.imageServicePhotoElementImage -> {
                print("KEK")
                // развертывание на всю активити
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_service_photo, container, false)
    }

    companion object {
        private const val TAG = "DBInf"
        const val FILE_PATH = "file path"
        const val BITMAP = "bitmap"

        @JvmStatic
        fun newInstance(
            bitmap: Bitmap,
            filePath: String
        ) =
            ServicePhotoFragment()
                .apply {
                arguments = Bundle().apply {
                    putParcelable(BITMAP, bitmap)
                    putString(FILE_PATH, filePath)
                }
            }
    }
}
