package com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.logIn

interface ProfileRepository {
    fun updateProfileData()
    fun getCountOfRates() : Long
}