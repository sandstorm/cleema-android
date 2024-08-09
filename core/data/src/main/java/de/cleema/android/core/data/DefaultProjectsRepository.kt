/*
 * Created by Kumpels and Friends on 2022-12-15
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.data

import de.cleema.android.core.data.network.ProjectsDataSource
import de.cleema.android.core.data.network.responses.ProjectResponse
import de.cleema.android.core.data.network.responses.ProjectResponse.GoalType.*
import de.cleema.android.core.data.network.responses.toPartner
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.net.URL
import java.util.*

class DefaultProjectsRepository(
    private val projectsDataSource: ProjectsDataSource,
    private val baseURL: URL,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ProjectsRepository {
    private val projectsChannel: Channel<Result<List<de.cleema.android.core.models.Project>>> = Channel()
    private val broadCaster: de.cleema.android.core.common.BroadCaster<UUID> =
        de.cleema.android.core.common.BroadCaster()

    init {
        CoroutineScope(ioDispatcher).launch {
            projectsChannel.send(fetchProjects())
        }
    }

    override fun getProjectsStream(
        regionId: UUID?,
        joined: Boolean?
    ): Flow<Result<List<de.cleema.android.core.models.Project>>> = flow {
        emit(fetchProjects(regionId, joined))
        broadCaster.events.map {
            fetchProjects(regionId, joined)
        }.collect(this)
    }.flowOn(ioDispatcher)

    override fun getFavedProjectsStream() = flow {
        emit(fetchFavedProjects())
        broadCaster.events.map {
            fetchFavedProjects()
        }.collect(this)
    }.flowOn(ioDispatcher)

    override fun getProjectStream(projectID: UUID): Flow<Result<de.cleema.android.core.models.Project>> = flow {
        emit(fetchProject(projectID))
        broadCaster.events.filter { it == projectID }.map {
            fetchProject(it)
        }.collect(this)
    }.flowOn(ioDispatcher)

    override suspend fun join(projectID: UUID) {
        projectsDataSource.joinProject(projectID).onSuccess {
            broadCaster.post(projectID)
        }
    }

    override suspend fun leave(projectID: UUID) {
        projectsDataSource.leaveProject(projectID).onSuccess {
            broadCaster.post(projectID)
        }
    }

    override suspend fun fav(projectID: UUID, status: Boolean) {
        projectsDataSource.favProject(projectID, status).onSuccess {
            broadCaster.post(projectID)
        }
    }

    private suspend fun fetchProjects(
        regionId: UUID? = null,
        joined: Boolean? = null
    ): Result<List<de.cleema.android.core.models.Project>> =
        projectsDataSource.getProjects(regionId, joined)
            .map { responses ->
                responses.mapNotNull {
                    return@mapNotNull it.toProject(baseURL)
                }
            }

    private suspend fun fetchFavedProjects(): Result<List<de.cleema.android.core.models.Project>> =
        projectsDataSource.getFavedProjects()
            .map { responses -> responses.mapNotNull { it.toProject(baseURL) } }

    private suspend fun fetchProject(projectId: UUID) = projectsDataSource.getProject(projectId).mapCatching {
        it.toProject(baseURL) ?: throw IllegalArgumentException("No project with $projectId found")
    }

}

fun ProjectResponse.toProject(baseURL: URL): de.cleema.android.core.models.Project? {
    val partner = partner ?: return null
    val region = region ?: return null
    val goal = when (goalType) {
        involvement -> {
            goalInvolvement?.let {
                de.cleema.android.core.models.Goal.Involvement(
                    it.currentParticipants,
                    it.maxParticipants,
                    joined
                )
            } ?: return null
        }
        funding -> {
            goalFunding?.let {
                de.cleema.android.core.models.Goal.Funding(
                    it.currentAmount,
                    it.totalAmount
                )
            } ?: return null
        }
        information -> de.cleema.android.core.models.Goal.Information
    }

    return de.cleema.android.core.models.Project(
        id = uuid,
        title = title,
        description = description,
        summary = summary,
        date = startDate,
        partner = partner.toPartner(baseURL),
        region = region.toRegion(),
        teaserImage = teaserImage?.toImage(baseURL),
        image = image?.toImage(baseURL),
        location = location?.toLocation(),
        isFaved = isFaved,
        goal = goal,
        phase = phase.toPhase()
    )
}

private fun ProjectResponse.Phase.toPhase(): de.cleema.android.core.models.Phase = when (this) {
    ProjectResponse.Phase.pre -> de.cleema.android.core.models.Phase.Pre
    ProjectResponse.Phase.running -> de.cleema.android.core.models.Phase.Within
    ProjectResponse.Phase.post -> de.cleema.android.core.models.Phase.Post
    ProjectResponse.Phase.cancelled -> de.cleema.android.core.models.Phase.Cancelled
}
