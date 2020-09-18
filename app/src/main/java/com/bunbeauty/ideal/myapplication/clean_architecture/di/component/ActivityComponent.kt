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

@ActivityScope
@Component(
    dependencies = [AppComponent::class],
    modules = [ActivityModule::class]
)
interface ActivityComponent {

}
