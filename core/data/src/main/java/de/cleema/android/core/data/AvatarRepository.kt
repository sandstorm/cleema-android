/*
 * Created by Kumpels and Friends on 2023-01-24
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */
package de.cleema.android.core.data

import kotlinx.coroutines.flow.Flow

interface AvatarRepository {
    fun getAvatarsStream(): Flow<Result<List<de.cleema.android.core.models.IdentifiedImage>>>
}
