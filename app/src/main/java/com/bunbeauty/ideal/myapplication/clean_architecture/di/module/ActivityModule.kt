package com.bunbeauty.ideal.myapplication.clean_architecture.di.module

import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.dbInstance.LocalDatabase
import com.bunbeauty.ideal.myapplication.clean_architecture.di.scope.ActivityScope
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.api.FiguringServicePointsApi
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.api.StringApi
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.api.VerifyPhoneNumberApi
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.fragments.profile.OrdersFragment
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.fragments.profile.ServicesFragment
import dagger.Module
import dagger.Provides

@Module
class ActivityModule {
    //Fragments
    @Provides
    @ActivityScope
    fun provideOrdersFragment() = OrdersFragment()

    @Provides
    @ActivityScope
    fun provideServicesFragment() = ServicesFragment()

    //APIs
    @Provides
    @ActivityScope
    fun provideFigureServicePointsApi() = FiguringServicePointsApi()

    @Provides
    @ActivityScope
    fun provideVerifyPhoneNumberApi() = VerifyPhoneNumberApi()

    @Provides
    @ActivityScope
    fun provideStringApi() = StringApi()

    @Provides
    @ActivityScope
    fun provideUserDao(localDatabase: LocalDatabase) = localDatabase.getUserDao()

    @Provides
    @ActivityScope
    fun provideServiceDao(localDatabase: LocalDatabase) = localDatabase.getServiceDao()

    @Provides
    @ActivityScope
    fun provideTagDao(localDatabase: LocalDatabase) = localDatabase.getTagDao()

    @Provides
    @ActivityScope
    fun providePhotoDao(localDatabase: LocalDatabase) = localDatabase.getPhotoDao()

    @Provides
    @ActivityScope
    fun provideCodeDao(localDatabase: LocalDatabase) = localDatabase.getCodeDao()

    @Provides
    @ActivityScope
    fun provideDialogDao(localDatabase: LocalDatabase) = localDatabase.getDialogDao()
}