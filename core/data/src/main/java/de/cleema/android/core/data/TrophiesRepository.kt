/*
 * Created by Kumpels and Friends on 2022-12-19
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.data

import kotlinx.coroutines.flow.Flow
import java.util.*

interface TrophiesRepository {
    fun getTrophiesStream(): Flow<Result<List<de.cleema.android.core.models.Trophy>>>
    fun getTrophyStream(trophyId: UUID): Flow<Result<de.cleema.android.core.models.Trophy>>
}
