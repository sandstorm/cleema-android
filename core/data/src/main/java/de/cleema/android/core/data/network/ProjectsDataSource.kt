/*
 * Created by Kumpels and Friends on 2022-12-15
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.data.network

import de.cleema.android.core.data.network.responses.ProjectResponse
import java.util.*

interface ProjectsDataSource {
    suspend fun getProjects(regionId: UUID? = null, joined: Boolean? = null): Result<List<ProjectResponse>>
    suspend fun getProject(projectId: UUID): Result<ProjectResponse>
    suspend fun getFavedProjects(): Result<List<ProjectResponse>>
    suspend fun favProject(projectId: UUID, faved: Boolean): Result<ProjectResponse>
    suspend fun joinProject(projectId: UUID): Result<ProjectResponse>
    suspend fun leaveProject(projectId: UUID): Result<ProjectResponse>
}
