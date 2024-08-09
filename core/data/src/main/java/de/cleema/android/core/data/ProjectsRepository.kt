/*
 * Created by Kumpels and Friends on 2022-12-15
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.data

import kotlinx.coroutines.flow.Flow
import java.util.*

interface ProjectsRepository {
    fun getProjectsStream(
        regionId: UUID?,
        joined: Boolean? = null
    ): Flow<Result<List<de.cleema.android.core.models.Project>>>

    fun getProjectStream(projectID: UUID): Flow<Result<de.cleema.android.core.models.Project>>
    fun getFavedProjectsStream(): Flow<Result<List<de.cleema.android.core.models.Project>>>
    suspend fun join(projectID: UUID)
    suspend fun leave(projectID: UUID)
    suspend fun fav(projectID: UUID, status: Boolean)
}
