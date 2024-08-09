package de.cleema.android.core.data.network

import de.cleema.android.core.data.network.responses.ChallengeResponse
import de.cleema.android.core.data.network.responses.ChallengeTemplateResponse
import de.cleema.android.core.models.EditChallenge
import java.util.*

interface ChallengesDataSource {
    suspend fun getChallenges(regionId: UUID? = null): Result<List<ChallengeResponse>>
    suspend fun getChallenge(challengeId: UUID): Result<ChallengeResponse>
    suspend fun joinChallenge(challengeId: UUID): Result<ChallengeResponse>
    suspend fun leaveChallenge(challengeId: UUID): Result<ChallengeResponse>
    suspend fun getJoinedChallenges(): Result<List<ChallengeResponse>>
    suspend fun answerChallenge(
        challengeId: UUID,
        answers: Map<Int, de.cleema.android.core.models.JoinedChallenge.Answer>
    ): Result<ChallengeResponse>

    suspend fun getChallengeTemplates(): Result<List<ChallengeTemplateResponse>>
    suspend fun createChallenge(template: EditChallenge, participants: Set<UUID>): Result<ChallengeResponse>
}
