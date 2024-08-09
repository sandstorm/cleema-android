/*
 * Created by Kumpels and Friends on 2022-12-15
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.data.network

import de.cleema.android.core.data.network.responses.MagazineItemResponse
import java.util.*

interface MagazineDataSource {
    suspend fun getMagazineItems(regionId: UUID?): Result<List<MagazineItemResponse>>
    suspend fun getFavedMagazineItems(): Result<List<MagazineItemResponse>>
    suspend fun getMagazineItem(itemId: UUID): Result<MagazineItemResponse>
    suspend fun favMagazineItem(itemId: UUID, faved: Boolean): Result<MagazineItemResponse>
    suspend fun markMagazineItemAsRead(itemId: UUID): Result<MagazineItemResponse>
}
