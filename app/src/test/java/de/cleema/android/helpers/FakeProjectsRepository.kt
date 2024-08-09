/*
 * Created by Kumpels and Friends on 2022-12-15
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.helpers

import de.cleema.android.core.data.ProjectsRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.util.*

class FakeProjectsRepository : ProjectsRepository {
    var joinedId: UUID? = null
    var leaveId: UUID? = null
    var invokedFavStatus: Pair<UUID, Boolean>? = null
    private val projectsFlow: MutableStateFlow<Map<UUID, List<de.cleema.android.core.models.Project>>> =
        MutableStateFlow(mapOf())
    val projectChannel: Channel<Result<de.cleema.android.core.models.Project>> = Channel()

    override fun getProjectsStream(
        regionId: UUID?,
        joined: Boolean?
    ): Flow<Result<List<de.cleema.android.core.models.Project>>> = projectsFlow.map {
        it.runCatching {
            it[regionId] ?: throw IllegalArgumentException("No projects for id")
        }
    }

    override fun getProjectStream(projectID: UUID): Flow<Result<de.cleema.android.core.models.Project>> =
        projectChannel.receiveAsFlow()

    override fun getFavedProjectsStream(): Flow<Result<List<de.cleema.android.core.models.Project>>> {
        TODO("Not yet implemented")
    }

    override suspend fun join(projectID: UUID) {
        joinedId = projectID
    }

    override suspend fun leave(projectID: UUID) {
        leaveId = projectID
    }

    override suspend fun fav(projectID: UUID, status: Boolean) {
        invokedFavStatus = projectID to status
    }

    fun stubProjects(regionId: UUID, projects: List<de.cleema.android.core.models.Project>) {
        projectsFlow.update {
            it.toMutableMap().apply {
                this[regionId] = projects
            }
        }
    }

    fun givenFailure(regionId: UUID) {
        projectsFlow.update {
            it.toMutableMap().apply {
                this.remove(regionId)
            }
        }
    }
}
