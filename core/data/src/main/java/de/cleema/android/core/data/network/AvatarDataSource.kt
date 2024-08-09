/*
 * Created by Kumpels and Friends on 2023-01-24
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.data.network

import de.cleema.android.core.data.network.responses.IdentifiedImageResponse

interface AvatarDataSource {
    suspend fun getAvatarList(): Result<List<IdentifiedImageResponse>>
}
