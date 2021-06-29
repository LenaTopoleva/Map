package com.lenatopoleva.map.model.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class DispatcherProvider: IDispatcherProvider {
    override fun io(): CoroutineDispatcher = Dispatchers.IO
}