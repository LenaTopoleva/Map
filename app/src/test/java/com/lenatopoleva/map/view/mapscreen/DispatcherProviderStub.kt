package com.lenatopoleva.map.view.mapscreen

import com.lenatopoleva.map.model.data.IDispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher

class DispatcherProviderStub(val dispatcher: CoroutineDispatcher): IDispatcherProvider {
    override fun io(): CoroutineDispatcher = dispatcher
}