package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.subscriptions

import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.SubscriptionAdapter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.subs.SubscribersInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.AppModule
import com.bunbeauty.ideal.myapplication.cleanArchitecture.di.DaggerAppComponent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.enums.ButtonTask
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.IBottomPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.ITopPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.SubscribersPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.SubscribersView
import javax.inject.Inject

class SubscribersActivity : MvpAppCompatActivity(), ITopPanel, IBottomPanel, SubscribersView {

    private lateinit var resultsSubscribersRecycleView: RecyclerView
    private lateinit var subscriptionAdapter: SubscriptionAdapter
    private lateinit var subsCountSubscribersText: TextView
    private lateinit var progressBarSubscribers: ProgressBar

    @Inject
    lateinit var subscribersInteractor: SubscribersInteractor

    @InjectPresenter
    lateinit var subscribersPresenter: SubscribersPresenter

    @ProvidePresenter
    internal fun provideProfilePresenter(): SubscribersPresenter {
        DaggerAppComponent.builder()
            .appModule(AppModule(application, intent))
            .build()
            .inject(this)
        return SubscribersPresenter(
            subscribersInteractor
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.subscribers)
        init()
        createPanels()
    }

    fun init() {
        resultsSubscribersRecycleView = findViewById(R.id.resultsSubscribersRecycleView)
        subsCountSubscribersText = findViewById(R.id.subsCountSubscribersText)
        progressBarSubscribers = findViewById(R.id.progressBarSubscribers)
    }

    private fun createPanels() {
        createTopPanel("Подписки", ButtonTask.NONE, supportFragmentManager)
        createBottomPanel(supportFragmentManager)
    }

}