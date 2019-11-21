package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback

interface IMainScreenCallback {
    fun returnMainScreenDataWithCreateCategory(mainScreenData:ArrayList<ArrayList<Any>>)
    fun returnMainScreenData(mainScreenData:ArrayList<ArrayList<Any>>)
}