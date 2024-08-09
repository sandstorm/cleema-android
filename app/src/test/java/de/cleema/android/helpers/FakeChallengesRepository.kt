/*
 * Created by Kumpels and Friends on 2022-12-08
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.helpers

import de.cleema.android.core.data.ChallengesRepository
import de.cleema.android.core.models.EditChallenge
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.util.*

class FakeChallengesRepository : ChallengesRepository {
    fun stubChallenges(regionId: UUID, challenges: List<de.cleema.android.core.models.Challenge>) {
        challengesFlow.update {
            it.toMutableMap().apply {
                this[regionId] = challenges
            }
        }
    }

    fun stubJoinedChallenges(challenges: List<de.cleema.android.core.models.JoinedChallenge>) {
        joinedChallengesChannel.trySend(Result.success(challenges))
    }

    var createdParticipants: Set<UUID>? = null
    var createdChallenge: EditChallenge? = null
    var answers: Pair<UUID, Map<Int, de.cleema.android.core.models.JoinedChallenge.Answer>>? = null
    var joinedChallengeChannel: Channel<Result<de.cleema.android.core.models.JoinedChallenge>> = Channel()
    var joinedChallengeId: UUID? = null
    var leaveChallengeId: UUID? = null
    var challengeMap: MutableMap<UUID, de.cleema.android.core.models.Challenge> = mutableMapOf()

    private val challengesFlow: MutableStateFlow<Map<UUID, List<de.cleema.android.core.models.Challenge>>> =
        MutableStateFlow(mapOf())
    private val joinedChallengesChannel: Channel<Result<List<de.cleema.android.core.models.JoinedChallenge>>> =
        Channel()

    private val templatesChannel = Channel<Result<List<EditChallenge>>>()
    var createChallengeResult: Result<de.cleema.android.core.models.JoinedChallenge> =
        Result.failure(RuntimeException("Not implemented"))

    override fun getChallengesStream(regionId: UUID): Flow<Result<List<de.cleema.android.core.models.Challenge>>> =
        challengesFlow.map {
            Result.success(it[regionId] ?: listOf())
        }

    override fun getChallengeStream(challengeId: UUID): Flow<Result<de.cleema.android.core.models.Challenge>> = flow {
        challengeMap[challengeId]?.let { emit(Result.success(it)) }
            ?: emit(Result.failure(IllegalArgumentException("No challenge with $challengeId found")))
    }

    override suspend fun leaveChallenge(challengeId: UUID) {
        leaveChallengeId = challengeId
    }

    override suspend fun joinChallenge(challengeId: UUID) {
        joinedChallengeId = challengeId
    }

    override fun getJoinedChallengesStream(): Flow<Result<List<de.cleema.android.core.models.JoinedChallenge>>> =
        joinedChallengesChannel.receiveAsFlow()

    override fun getJoinedChallengeStream(challengeId: UUID): Flow<Result<de.cleema.android.core.models.JoinedChallenge>> =
        joinedChallengeChannel.receiveAsFlow()

    override suspend fun answer(
        challengeId: UUID,
        answers: Map<Int, de.cleema.android.core.models.JoinedChallenge.Answer>
    ) {
        this.answers = challengeId to answers
    }

    override suspend fun createChallenge(
        challenge: EditChallenge,
        participants: Set<UUID>
    ): Result<de.cleema.android.core.models.JoinedChallenge> {
        createdChallenge = challenge
        createdParticipants = participants
        return createChallengeResult
    }

    override fun getTemplatesStream(): Flow<Result<List<EditChallenge>>> = templatesChannel.receiveAsFlow()

    fun stubTemplates(templates: List<EditChallenge>) {
        templatesChannel.trySend(Result.success(templates))
    }
}
