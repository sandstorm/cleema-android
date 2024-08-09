/*
 * Created by Kumpels and Friends on 2023-01-24
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.data.network.requests

import de.cleema.android.core.data.network.*
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class CreateChallengeRequest(
    val title: String,
    val teaserText: String,
    val description: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val kind: ApiChallengeKind,
    val isPublic: Boolean,
    val interval: ApiChallengeInterval,
    val region: IdRequest,
    val goalType: ApiGoalType,
    val goalSteps: ApiSteps?,
    val goalMeasurement: ApiGoalMeasurement?,
    // FIXME: this is a list of UUID but we use strings for Serializable compatibility
    val participants: List<String> = listOf(),
    val image: IdRequest?
)

