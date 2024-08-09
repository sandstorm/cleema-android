/*
 * Created by Kumpels and Friends on 2022-12-15
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.data

import kotlinx.coroutines.flow.Flow
import java.util.*

interface MagazineRepository {
    fun getMagazineItemsStream(regionId: UUID?): Flow<Result<List<de.cleema.android.core.models.MagazineItem>>>
    fun getMagazineItemStream(itemId: UUID): Flow<Result<de.cleema.android.core.models.MagazineItem>>
    fun getFavedMagazineItemsStream(): Flow<Result<List<de.cleema.android.core.models.MagazineItem>>>
    suspend fun fav(itemId: UUID, faved: Boolean)
    suspend fun markAsRead(itemId: UUID)
}
