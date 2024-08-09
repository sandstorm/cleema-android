/*
 * Created by Kumpels and Friends on 2022-11-21
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.data

import de.cleema.android.core.data.network.InfoDataSource
import de.cleema.android.core.models.DrawerRoute
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DefaultInfoRepository(
    private val infoDatasource: InfoDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : InfoRepository {
    override suspend fun getContent(route: DrawerRoute): Result<String> = withContext(ioDispatcher) {
        infoDatasource.getContent(route)
    }
}
