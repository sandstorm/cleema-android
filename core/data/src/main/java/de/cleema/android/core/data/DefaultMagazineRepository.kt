/*
 * Created by Kumpels and Friends on 2022-12-15
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.data

import de.cleema.android.core.data.network.MagazineDataSource
import de.cleema.android.core.data.network.responses.MagazineItemResponse
import de.cleema.android.core.models.MagazineItem.ItemType.NEWS
import de.cleema.android.core.models.MagazineItem.ItemType.TIP
import de.cleema.android.core.models.Size
import de.cleema.android.data.network.responses.scale
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import java.net.URL
import java.util.*

class DefaultMagazineRepository(
    private val magazineDataSource: MagazineDataSource,
    private val baseURL: URL,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : MagazineRepository {
    private val broadCaster: de.cleema.android.core.common.BroadCaster<UUID> =
        de.cleema.android.core.common.BroadCaster()

    override fun getMagazineItemsStream(regionId: UUID?): Flow<Result<List<de.cleema.android.core.models.MagazineItem>>> =
        flow {
            emit(fetchMagazineItems(regionId))

            broadCaster.events.map {
                fetchMagazineItems(regionId)
            }.collect(this)
        }.flowOn(ioDispatcher)

    override fun getMagazineItemStream(itemId: UUID): Flow<Result<de.cleema.android.core.models.MagazineItem>> = flow {
        emit(magazineDataSource.getMagazineItem(itemId).map { it.toItem(baseURL) })
        broadCaster.events.filter { it == itemId }.map {
            magazineDataSource.getMagazineItem(itemId).map { it.toItem(baseURL) }
        }.collect(this)
    }.flowOn(ioDispatcher)

    override fun getFavedMagazineItemsStream(): Flow<Result<List<de.cleema.android.core.models.MagazineItem>>> = flow {
        emit(fetchFavedMagazineItems())

        broadCaster.events.map {
            fetchFavedMagazineItems()
        }.collect(this)
    }.flowOn(ioDispatcher)

    private suspend fun fetchMagazineItems(regionId: UUID?): Result<List<de.cleema.android.core.models.MagazineItem>> =
        magazineDataSource.getMagazineItems(regionId).map { itemResponses -> itemResponses.map { it.toItem(baseURL) } }

    private suspend fun fetchFavedMagazineItems(): Result<List<de.cleema.android.core.models.MagazineItem>> =
        magazineDataSource.getFavedMagazineItems().map { itemResponses -> itemResponses.map { it.toItem(baseURL) } }

    override suspend fun fav(itemId: UUID, faved: Boolean) {
        magazineDataSource.favMagazineItem(itemId, faved).onSuccess {
            broadCaster.post(itemId)
        }
    }

    override suspend fun markAsRead(itemId: UUID) {
        // TODO: send received market item
        magazineDataSource.markMagazineItemAsRead(itemId).onSuccess {
            broadCaster.post(itemId)
        }
    }
}

private fun MagazineItemResponse.toItem(baseURL: URL): de.cleema.android.core.models.MagazineItem =
    de.cleema.android.core.models.MagazineItem(
        id = uuid,
        title = title,
        description = description,
        teaser = teaser ?: description,
        date = date,
        publishedAt = publishedAt,
        tags = tags.map { de.cleema.android.core.models.Tag(value = it.value) },
        image = image?.let {
            de.cleema.android.core.models.RemoteImage.of(
                url = URL(baseURL, it.url).toString(),
                size = Size(
                    it.width.toFloat() / it.scale.toFloat(),
                    it.height.toFloat() / it.scale.toFloat()
                )
            )
        },
        type = if (type == MagazineItemResponse.Type.news) NEWS else TIP,
        region = region?.toRegion(),
        faved = isFaved
    )
