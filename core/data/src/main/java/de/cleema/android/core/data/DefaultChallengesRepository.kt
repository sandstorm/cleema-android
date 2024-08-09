/*
 * Created by Kumpels and Friends on 2023-01-24
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.data

import de.cleema.android.core.common.BroadCaster
import de.cleema.android.core.data.network.*
import de.cleema.android.core.data.network.responses.ChallengeResponse
import de.cleema.android.core.data.network.responses.ChallengeTemplateResponse
import de.cleema.android.core.data.network.responses.toPartner
import de.cleema.android.core.models.Challenge
import de.cleema.android.core.models.Challenge.*
import de.cleema.android.core.models.Challenge.Interval.DAILY
import de.cleema.android.core.models.Challenge.Interval.WEEKLY
import de.cleema.android.core.models.Challenge.Kind.Group
import de.cleema.android.core.models.Challenge.Kind.User
import de.cleema.android.core.models.Challenge.Unit.KILOGRAMS
import de.cleema.android.core.models.Challenge.Unit.KILOMETERS
import de.cleema.android.core.models.EditChallenge
import de.cleema.android.core.models.JoinedChallenge.Answer
import de.cleema.android.core.models.JoinedChallenge.Answer.FAILED
import de.cleema.android.core.models.JoinedChallenge.Answer.SUCCEEDED
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone.Companion.UTC
import kotlinx.datetime.toLocalDateTime
import java.net.URL
import java.util.*
import javax.inject.Inject
import kotlin.time.Duration.Companion.days

class DefaultChallengesRepository @Inject constructor(
    private val dataSource: ChallengesDataSource,
    private val ioDispatcher: CoroutineDispatcher,
    private val baseURL: URL,
) : ChallengesRepository {
    private val broadcaster: BroadCaster<Result<UUID>> =
        de.cleema.android.core.common.BroadCaster()

    override fun getChallengesStream(regionId: UUID): Flow<Result<List<Challenge>>> =
        flow {
            emit(fetchChanges(regionId))

            broadcaster.events.map {
                fetchChanges(regionId)
            }.collect(this)
        }.flowOn(ioDispatcher)

    override fun getChallengeStream(challengeId: UUID): Flow<Result<Challenge>> = flow {
        emit(fetchChallenge(challengeId))

        broadcaster.events.map { it ->
            it.fold(
                onSuccess = {
                    fetchChallenge(it)
                },
                onFailure = Result.Companion::failure
            )
        }.collect(this)
    }.flowOn(ioDispatcher)

    override fun getJoinedChallengesStream(): Flow<Result<List<de.cleema.android.core.models.JoinedChallenge>>> = flow {
        emit(fetchJoinedChallenges())

        broadcaster.events.map {
            fetchJoinedChallenges()
        }.collect(this)
    }.flowOn(ioDispatcher)

    override suspend fun leaveChallenge(challengeId: UUID) {
        dataSource.leaveChallenge(challengeId)
            .onSuccess {
                broadcaster.post(Result.success(challengeId))
            }
            .onFailure { broadcaster.post(Result.failure(it)) }
    }

    override suspend fun joinChallenge(challengeId: UUID) {
        dataSource.joinChallenge(challengeId)
            .onSuccess {
                broadcaster.post(Result.success(challengeId))
            }
            .onFailure {
                broadcaster.post(Result.failure(it))
            }
    }

    override fun getJoinedChallengeStream(challengeId: UUID): Flow<Result<de.cleema.android.core.models.JoinedChallenge>> =
        flow {
            emit(fetchJoinedChallenge(challengeId))

            broadcaster.events.map { it ->
                it.fold(onSuccess = { fetchJoinedChallenge(it) }, onFailure = { Result.failure(it) })
            }.collect(this)
        }.flowOn(ioDispatcher)

    override suspend fun answer(challengeId: UUID, answers: Map<Int, Answer>) {
        dataSource.answerChallenge(challengeId, answers)
            .onSuccess {
                broadcaster.post(Result.success(challengeId))
            }
            .onFailure { broadcaster.post(Result.failure(it)) }
    }

    override fun getTemplatesStream(): Flow<Result<List<EditChallenge>>> = flow {
        emit(dataSource.getChallengeTemplates().map { res -> res.mapNotNull { it.toModel(baseURL) } })
    }.flowOn(ioDispatcher)

    override suspend fun createChallenge(
        challenge: EditChallenge,
        participants: Set<UUID>
    ): Result<de.cleema.android.core.models.JoinedChallenge> =
        dataSource.createChallenge(challenge, participants)
            .mapCatching { it.toJoinedChallenge(baseURL) ?: throw RuntimeException("Could not create challenge") }
            .onSuccess {
                broadcaster.post(Result.success(it.challenge.id))
            }.onFailure {
                broadcaster.post(Result.failure(it))
            }

    private suspend fun fetchChanges(regionId: UUID) =
        dataSource.getChallenges(regionId).map { responses -> responses.mapNotNull { it.toChallenge(baseURL) } }

    private suspend fun fetchChallenge(challengeId: UUID) = dataSource.getChallenge(challengeId).mapCatching {
        it.toChallenge(baseURL) ?: throw IllegalArgumentException("No challenge with $challengeId fount!")
    }

    private suspend fun fetchJoinedChallenges() =
        dataSource.getJoinedChallenges().map { responses -> responses.mapNotNull { it.toJoinedChallenge(baseURL) } }

    private suspend fun fetchJoinedChallenge(challengeId: UUID) =
        dataSource.getChallenge(challengeId).mapCatching {
            it.toJoinedChallenge(baseURL) ?: throw IllegalArgumentException("No challenge with $challengeId found!")
        }
}

private fun ChallengeResponse.toChallenge(baseURL: URL): Challenge? {
    val type = toType()
    val kind = toKind(baseURL)
    if (type == null || kind == null) return null
    return de.cleema.android.core.models.Challenge.of(
        id = uuid,
        title = title,
        teaserText = teaserText ?: "",
        description = description,
        type = type,
        interval.toInterval(),
        startDate = startDate,
        endDate = endDate,
        isPublic = isPublic,
        joined = joined,
        kind = kind,
        region = region.toRegion(),
        numberOfUsersJoined = usersJoined?.count() ?: 0,
        image = image?.toImage(baseURL),
        collectiveGoalAmount = collectiveGoalAmount,
        collectiveProgress = collectiveProgress,
    )
}

private fun ChallengeResponse.toKind(baseURL: URL): Challenge.Kind? = when {
    kind == ApiChallengeKind.user -> User
    kind == ApiChallengeKind.partner && partner != null -> Challenge.Kind.Partner(partner.toPartner(baseURL))
    kind == ApiChallengeKind.group -> Group(userProgress?.let { responses -> responses.map { it.toProgress(baseURL) } }
        ?: listOf())
    kind == ApiChallengeKind.collective && partner != null -> Challenge.Kind.Collective(partner.toPartner(baseURL)) // TODO
    else -> null
}

private fun ChallengeResponse.UserProgressResponse.toProgress(baseURL: URL): de.cleema.android.core.models.UserProgress =
    de.cleema.android.core.models.UserProgress(
        totalAnswers = totalAnswers, succeededAnswers = succeededAnswers,
        user.toUser(baseURL)
    )

private fun ApiChallengeInterval.toInterval(): Interval = when (this) {
    ApiChallengeInterval.daily -> DAILY
    ApiChallengeInterval.weekly -> WEEKLY
}

private fun ChallengeResponse.toType(): GoalType? = when {
    goalType == ApiGoalType.steps && goalSteps != null ->
        GoalType.Steps(goalSteps.count.toInt())
    goalType == ApiGoalType.measurement && goalMeasurement != null ->
        GoalType.Measurement(
            goalMeasurement.value.toInt(),
            goalMeasurement.unit.toUnit()
        )
    else -> null
}

private fun ApiGoalMeasurement.Unit.toUnit(): Challenge.Unit = when (this) {
    ApiGoalMeasurement.Unit.kg -> KILOGRAMS
    ApiGoalMeasurement.Unit.km -> KILOMETERS
}

private fun ChallengeResponse.AnswerResponse.Status.toAnswer(): Answer =
    when (this) {
        ChallengeResponse.AnswerResponse.Status.succeeded -> SUCCEEDED
        ChallengeResponse.AnswerResponse.Status.failed -> FAILED
    }

private fun ChallengeResponse.toJoinedChallenge(baseURL: URL): de.cleema.android.core.models.JoinedChallenge? =
    toChallenge(baseURL)?.let { challenge ->
        if (!challenge.joined) {
            return null
        }
        val answers =
            joinedChallenge?.answers?.associate { it.dayIndex to it.answer.toAnswer() } ?: mapOf()
        de.cleema.android.core.models.JoinedChallenge(challenge, answers)
    }

private fun ChallengeTemplateResponse.toModel(baseURL: URL): EditChallenge? {
    return toType()?.let { goalType ->
        toChallengeKind()?.let { kind ->
            EditChallenge(
                title,
                teaserText ?: "",
                description,
                goalType,
                interval = interval.toInterval(),
                start = Clock.System.now().toLocalDateTime(UTC).date,
                end = Clock.System.now().plus(30.days).toLocalDateTime(UTC).date,
                isPublic = isPublic,
                regionId = null,
                kind = kind,
                logo = partner?.logo?.toImage(baseURL),
                image = image?.toImage(baseURL)
            )
        }
    }
}

private fun ChallengeTemplateResponse.toChallengeKind(): Kind? =
    when (this.kind) {
        ApiChallengeKind.user -> User

        ApiChallengeKind.collective -> {
            null
        }

        ApiChallengeKind.partner -> {
            // TODO: it is not possible to create partner challenges from templates?
            null
        }

        ApiChallengeKind.group -> Group(listOf())
    }

private fun ChallengeTemplateResponse.toType(): GoalType? = when {
    goalType == ApiGoalType.steps && goalSteps != null ->
        GoalType.Steps(goalSteps.count.toInt())
    goalType == ApiGoalType.measurement && goalMeasurement != null ->
        GoalType.Measurement(
            goalMeasurement.value.toInt(),
            goalMeasurement.unit.toUnit()
        )
    else -> null
}
