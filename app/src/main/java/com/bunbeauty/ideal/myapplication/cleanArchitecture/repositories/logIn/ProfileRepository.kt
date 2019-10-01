package com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.logIn

interface ProfileRepository {
    fun loadProfileData(ownerId: String)
    fun updateProfileData()
    fun getCountOfRates() : Long
}