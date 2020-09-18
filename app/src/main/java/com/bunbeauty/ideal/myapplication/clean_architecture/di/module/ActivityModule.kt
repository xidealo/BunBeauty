package com.bunbeauty.ideal.myapplication.clean_architecture.di.module

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.dbInstance.LocalDatabase
import com.bunbeauty.ideal.myapplication.clean_architecture.di.scope.ActivityScope
import com.bunbeauty.ideal.myapplication.clean_architecture.di.scope.AppScope
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.api.FiguringServicePointsApi
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.api.StringApi
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.api.VerifyPhoneNumberApi
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.fragments.profile.OrdersFragment
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.fragments.profile.ServicesFragment
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ActivityModule {

}