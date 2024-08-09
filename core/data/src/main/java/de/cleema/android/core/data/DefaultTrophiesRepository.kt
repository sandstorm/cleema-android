/*
 * Created by Kumpels and Friends on 2023-01-27
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.data

import de.cleema.android.core.data.network.TrophiesDataSource
import de.cleema.android.core.data.network.responses.TrophyItemResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.net.URL
import java.util.*
import javax.inject.Inject

class DefaultTrophiesRepository @Inject constructor(
    private val trophiesDataSource: TrophiesDataSource,
    private val ioDispatcher: CoroutineDispatcher,
    private val baseURL: URL
) : TrophiesRepository {
    private val broadCaster = de.cleema.android.core.common.BroadCaster<Result<UUID>>()

    override fun getTrophiesStream(): Flow<Result<List<de.cleema.android.core.models.Trophy>>> = flow {
        emit(fetchTrophies())
        broadCaster.events.map {
            fetchTrophies()
        }.collect(this)
    }.flowOn(ioDispatcher)

    override fun getTrophyStream(trophyId: UUID): Flow<Result<de.cleema.android.core.models.Trophy>> = flow {
        emit(fetchTrophy(trophyId))
        broadCaster.events.map {
            it.fold(
                onSuccess = { id ->
                    fetchTrophy(id)
                },
                onFailure = Result.Companion::failure
            )
        }.collect(this)
    }.flowOn(ioDispatcher)

    private suspend fun fetchTrophies(): Result<List<de.cleema.android.core.models.Trophy>> =
        trophiesDataSource.getTrophies()
            .map { responses -> responses.map { it.toTrophy(baseURL) } }

    private suspend fun fetchTrophy(trophyId: UUID): Result<de.cleema.android.core.models.Trophy> =
        trophiesDataSource.getTrophy(trophyId).map { it.toTrophy(baseURL) }
}

fun TrophyItemResponse.toTrophy(baseURL: URL): de.cleema.android.core.models.Trophy {
    return de.cleema.android.core.models.Trophy(
        id = trophy.uuid,
        date = date,
        title = trophy.title,
        image = trophy.image.toImage(baseURL)
    )
}
