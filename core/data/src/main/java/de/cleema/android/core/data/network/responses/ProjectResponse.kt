/*
 * Created by Kumpels and Friends on 2022-12-15
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.data.network.responses

import de.cleema.android.core.common.UUIDSerializer
import de.cleema.android.data.network.responses.ImageResponse
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class ProjectResponse(
    @Serializable(with = UUIDSerializer::class)
    val uuid: UUID,
    val title: String,
    val summary: String,
    val description: String,
    val startDate: Instant,
    val createdAt: Instant,
    val updatedAt: Instant,
    val publishedAt: Instant?,
    val locale: String,
    val goalType: GoalType,
    val phase: Phase,
    val conclusion: String?,
    val region: RegionResponse?,
    val partner: PartnerResponse?,
    val goalInvolvement: GoalInvolvement?,
    val goalFunding: GoalFunding?,
    val location: LocationResponse?,
    val image: ImageResponse?,
    val teaserImage: ImageResponse?,
    val isFaved: Boolean,
    val joined: Boolean
) {
    @Serializable
    enum class GoalType {
        involvement,
        funding,
        information
    }

    @Serializable
    enum class Phase {
        pre, running, post, cancelled
    }
}

@Serializable
data class GoalFunding(
    val currentAmount: Int,
    val totalAmount: Int
)

@Serializable
data class GoalInvolvement(
    val currentParticipants: Int,
    val maxParticipants: Int
)


