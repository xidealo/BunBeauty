package com.bunbeauty.ideal.myapplication.cleanArchitecture.di

import android.app.Application
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.FiguringServicePoints
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.api.VerifyPhoneNumberApi
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dbInstance.LocalDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val app: Application) {

    @Provides
    @Singleton
    fun provideUserDao() = LocalDatabase.getDatabase(app).getUserDao()

    @Provides
    @Singleton
    fun provideServiceDao() = LocalDatabase.getDatabase(app).getServiceDao()

    @Provides
    @Singleton
    fun provideTagDao() = LocalDatabase.getDatabase(app).getTagDao()

    @Provides
    @Singleton
    fun providePhotoDao() = LocalDatabase.getDatabase(app).getPhotoDao()

    @Provides
    @Singleton
    fun provideCodeDao() = LocalDatabase.getDatabase(app).getCodeDao()

    @Provides
    @Singleton
    fun provideDialogDao() = LocalDatabase.getDatabase(app).getDialogDao()

    //APIs
    @Provides
    fun provideFigureServicePointsApi() = FiguringServicePoints()

    @Provides
    fun provideVerifyPhoneNumberApi() = VerifyPhoneNumberApi()

}