package com.bunbeauty.ideal.myapplication.clean_architecture.di.component

import com.bunbeauty.ideal.myapplication.clean_architecture.di.module.FragmentModule
import com.bunbeauty.ideal.myapplication.clean_architecture.di.scope.FragmentScope
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.fragments.profile.OrdersFragment
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.fragments.profile.ServicesFragment
import dagger.Component

@FragmentScope
@Component(
    modules = [FragmentModule::class],
    dependencies = [AppComponent::class]
)
interface FragmentComponent {
    fun inject(ordersFragment: OrdersFragment)
    fun inject(servicesFragment: ServicesFragment)
}
