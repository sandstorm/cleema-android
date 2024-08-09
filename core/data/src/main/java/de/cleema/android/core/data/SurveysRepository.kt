/*
 * Created by Kumpels and Friends on 2022-12-12
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.data

import de.cleema.android.core.data.network.SurveysDataSource
import de.cleema.android.core.data.network.responses.SurveyResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.util.*

interface SurveysRepository {
    fun getSurveysStream(): Flow<Result<List<de.cleema.android.core.models.Survey>>>
    suspend fun evaluate(id: UUID)
    suspend fun participate(id: UUID)
}

class DefaultSurveysRepository(
    private val dataSource: SurveysDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SurveysRepository {
    private val broadCaster: de.cleema.android.core.common.BroadCaster<UUID> =
        de.cleema.android.core.common.BroadCaster()

    override fun getSurveysStream(): Flow<Result<List<de.cleema.android.core.models.Survey>>> = flow {
        emit(fetchSurveys())
        broadCaster.events.map {
            fetchSurveys()
        }.collect(this)
    }.flowOn(ioDispatcher)

    override suspend fun evaluate(id: UUID) {
        dataSource.evaluate(id).onSuccess {
            broadCaster.post(id)
        }
    }

    override suspend fun participate(id: UUID) {
        dataSource.participate(id).onSuccess {
            broadCaster.post(id)
        }
    }

    private suspend fun fetchSurveys(): Result<List<de.cleema.android.core.models.Survey>> =
        dataSource.getSurveys().map { it.mapNotNull(SurveyResponse::toSurvey) }
}

private fun SurveyResponse.toSurvey(): de.cleema.android.core.models.Survey? = if (finished && evaluationUrl != null) {
    de.cleema.android.core.models.Survey(
        id = uuid,
        title = title,
        description = description,
        state = de.cleema.android.core.models.SurveyState.Evaluation(evaluationUrl)
    )
} else if (!finished && surveyUrl != null) {
    de.cleema.android.core.models.Survey(
        id = uuid,
        title = title,
        description = description,
        state = de.cleema.android.core.models.SurveyState.Participation(surveyUrl)
    )
} else {
    null
}

