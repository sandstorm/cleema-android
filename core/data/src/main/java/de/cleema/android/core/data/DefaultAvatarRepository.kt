/*
 * Created by Kumpels and Friends on 2023-01-24
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.data

import de.cleema.android.core.data.network.AvatarDataSource
import de.cleema.android.core.data.network.responses.IdentifiedImageResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.net.URL

class DefaultAvatarRepository(
    private val dataSource: AvatarDataSource,
    private val ioDispatcher: CoroutineDispatcher,
    private val baseURL: URL,
) : AvatarRepository {
    override fun getAvatarsStream(): Flow<Result<List<de.cleema.android.core.models.IdentifiedImage>>> = flow {
        emit(dataSource.getAvatarList().map { responses ->
            responses.map { it.toAvatar(baseURL) }
        })
    }.flowOn(ioDispatcher)
}

fun IdentifiedImageResponse.toAvatar(baseURL: URL) =
    de.cleema.android.core.models.IdentifiedImage(uuid, image.toImage(baseURL))
