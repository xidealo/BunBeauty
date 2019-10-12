package com.bunbeauty.ideal.myapplication.cleanArchitecture.business

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

abstract class BaseInteractor : CoroutineScope {
    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main
}