package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback

interface MainScreenCallback {
    fun returnMainScreenDataWithCreateCategory(mainScreenData:ArrayList<ArrayList<Any>>)
    fun returnMainScreenData(mainScreenData:ArrayList<ArrayList<Any>>)
}