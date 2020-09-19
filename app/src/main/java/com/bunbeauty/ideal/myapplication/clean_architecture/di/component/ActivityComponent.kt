package com.bunbeauty.ideal.myapplication.clean_architecture.di.component

import com.bunbeauty.ideal.myapplication.clean_architecture.di.module.*
import com.bunbeauty.ideal.myapplication.clean_architecture.di.scope.ActivityScope
import com.bunbeauty.ideal.myapplication.clean_architecture.di.scope.AppScope
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.ScheduleActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.SessionsActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.chat.DialogsActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.chat.MessagesActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.comments.CreationCommentActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.comments.CurrentCommentActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.comments.ServiceCommentsActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.comments.UserCommentsActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.create_service.CreationServiceActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.editing.EditProfileActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.editing.EditServiceActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.log_in.AuthorizationActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.log_in.RegistrationActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.log_in.VerifyPhoneNumberActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.profile.ProfileActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.search_service.MainScreenActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.service.ServiceActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.subscriptions.SubscriptionsActivity
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.fragments.PremiumFragment
import dagger.Component
import javax.inject.Singleton

@ActivityScope
@Component(
    dependencies = [AppComponent::class],
    modules = [
        ActivityModule::class,
        RepositoryModule::class,
        InteractorModule::class,
        FirebaseModule::class,
        AdapterModule::class
    ]
)
interface ActivityComponent {

    fun inject(premiumFragment: PremiumFragment)
    fun inject(authorizationActivity: AuthorizationActivity)
    fun inject(verifyPhoneNumberActivity: VerifyPhoneNumberActivity)
    fun inject(registrationActivity: RegistrationActivity)
    fun inject(profileActivity: ProfileActivity)
    fun inject(creationServiceActivity: CreationServiceActivity)
    fun inject(mainScreenActivity: MainScreenActivity)
    fun inject(serviceActivity: ServiceActivity)
    fun inject(editProfileActivity: EditProfileActivity)
    fun inject(scheduleActivity: ScheduleActivity)
    fun inject(dialogsActivity: DialogsActivity)
    fun inject(messagesActivity: MessagesActivity)
    fun inject(subscriptionsActivity: SubscriptionsActivity)
    fun inject(editServiceActivity: EditServiceActivity)
    fun inject(userCommentsActivity: UserCommentsActivity)
    fun inject(currentCommentActivity: CurrentCommentActivity)
    fun inject(creationCommentActivity: CreationCommentActivity)
    fun inject(serviceCommentsActivity: ServiceCommentsActivity)
    fun inject(sessionsActivity: SessionsActivity)

}
