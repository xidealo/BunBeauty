package com.bunbeauty.ideal.myapplication.clean_architecture.domain.api

class StringApi {

    companion object {
        fun addFirstZero(input: String, lineSize: Int): String {
            return "0".repeat(lineSize - input.length) + input
        }
    }
}