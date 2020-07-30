package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService.iSearchService

import android.widget.TextView
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.MainScreenPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.MainScreenData
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.google.android.material.chip.Chip

interface IMainScreenDataInteractor {
    fun createMainScreenData(
        cacheUserList: ArrayList<User>,
        cacheServiceList: ArrayList<Service>
    )

    fun getMainScreenData(mainScreenPresenterCallback: MainScreenPresenterCallback)

    fun showCurrentMainScreen(mainScreenPresenterCallback: MainScreenPresenterCallback)

    fun getMainScreenData(
        tagText: Chip,
        selectedTagsArray: ArrayList<String>,
        mainScreenPresenterCallback: MainScreenPresenterCallback
    )

    fun getMainScreenData(
        category: String,
        mainScreenPresenterCallback: MainScreenPresenterCallback
    )

    fun isSelectedCategory(category: String): Boolean
}