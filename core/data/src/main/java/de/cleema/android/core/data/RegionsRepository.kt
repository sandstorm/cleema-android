/*
 * Created by Kumpels and Friends on 2022-11-15
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.data

import de.cleema.android.core.data.network.RegionDataSource
import de.cleema.android.core.data.network.responses.RegionResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

interface RegionsRepository {
    fun getRegionsStream(): Flow<Result<List<de.cleema.android.core.models.Region>>>
}

class DefaultRegionsRepository(
    private val networkDataSource: RegionDataSource,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : RegionsRepository {
    private val _state: MutableStateFlow<Result<List<de.cleema.android.core.models.Region>>> =
        MutableStateFlow(Result.success(listOf()))

    override fun getRegionsStream(): Flow<Result<List<de.cleema.android.core.models.Region>>> = _state.asStateFlow()

    init {
        CoroutineScope(ioDispatcher).launch {
            _state.value = networkDataSource.getRegions(null).map { it.map(RegionResponse::toRegion) }
        }
    }
}
