package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.api

class StringApi {

    fun addFirstZero(input: String, lineSize: Int): String {
        return "0".repeat(lineSize - input.length) + input
    }
}