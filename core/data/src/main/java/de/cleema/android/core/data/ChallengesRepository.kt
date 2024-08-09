package de.cleema.android.core.data

import de.cleema.android.core.models.EditChallenge
import kotlinx.coroutines.flow.Flow
import java.util.*

interface ChallengesRepository {
    fun getChallengesStream(regionId: UUID): Flow<Result<List<de.cleema.android.core.models.Challenge>>>
    fun getChallengeStream(challengeId: UUID): Flow<Result<de.cleema.android.core.models.Challenge>>
    fun getJoinedChallengesStream(): Flow<Result<List<de.cleema.android.core.models.JoinedChallenge>>>
    fun getJoinedChallengeStream(challengeId: UUID): Flow<Result<de.cleema.android.core.models.JoinedChallenge>>
    fun getTemplatesStream(): Flow<Result<List<EditChallenge>>>
    suspend fun leaveChallenge(challengeId: UUID)
    suspend fun joinChallenge(challengeId: UUID)
    suspend fun answer(challengeId: UUID, answers: Map<Int, de.cleema.android.core.models.JoinedChallenge.Answer>)
    suspend fun createChallenge(
        challenge: EditChallenge,
        participants: Set<UUID>
    ): Result<de.cleema.android.core.models.JoinedChallenge>
}
