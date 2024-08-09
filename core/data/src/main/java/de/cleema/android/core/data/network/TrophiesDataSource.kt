/*
 * Created by Kumpels and Friends on 2022-12-19
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.data.network

import de.cleema.android.core.data.network.responses.TrophyItemResponse
import java.util.*

interface TrophiesDataSource {
    suspend fun getTrophies(): Result<List<TrophyItemResponse>>
    suspend fun getTrophy(trophyId: UUID): Result<TrophyItemResponse>
}
