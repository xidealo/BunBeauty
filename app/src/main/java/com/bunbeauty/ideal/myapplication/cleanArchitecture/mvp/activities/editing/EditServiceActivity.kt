package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.editing

import android.os.Bundle
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity

class EditServiceActivity : MvpAppCompatActivity() {
    /* имя, адрес, описание, цена, (категории)
    дизайн
    переменная
    инит
    панели
    пресентер
    интерактор
    онкклик*/


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_service)
    }
}
