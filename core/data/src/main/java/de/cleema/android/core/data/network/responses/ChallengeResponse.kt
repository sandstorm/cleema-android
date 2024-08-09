/*
 * Created by Kumpels and Friends on 2023-01-24
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.data.network.responses

import de.cleema.android.core.common.UUIDSerializer
import de.cleema.android.core.data.network.*
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class ChallengeResponse(
    @Serializable(with = UUIDSerializer::class)
    val uuid: UUID,
    val title: String,
    val teaserText: String?,
    val description: String,
    val isPublic: Boolean,
    val startDate: Instant,
    val endDate: Instant,
    val kind: ApiChallengeKind,
    val interval: ApiChallengeInterval,
    val goalType: ApiGoalType,
    val goalSteps: ApiSteps?,
    val goalMeasurement: ApiGoalMeasurement?,
    val region: RegionResponse,
    val joined: Boolean,
    val joinedChallenge: JoinResponse?,
    val partner: PartnerResponse?,
    val collectiveGoalAmount: Int?,
    val collectiveProgress: Int?,
    val userProgress: List<UserProgressResponse>?,
    val usersJoined: List<SocialUserResponse>?,
    var image: IdentifiedImageResponse?
) {
    @Serializable
    data class JoinResponse(val answers: List<AnswerResponse>)

    @Serializable
    data class AnswerResponse(val answer: Status, val dayIndex: Int) {
        @Serializable
        enum class Status { succeeded, failed }
    }

    @Serializable
    data class UserProgressResponse(val totalAnswers: Int, val succeededAnswers: Int, val user: SocialUserResponse)
}
