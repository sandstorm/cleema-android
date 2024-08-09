/*
 * Created by Kumpels and Friends on 2023-01-24
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.data.network.responses

import de.cleema.android.core.data.network.*

@kotlinx.serialization.Serializable
data class ChallengeTemplateResponse(
    val title: String,
    val teaserText: String?,
    val description: String,
    val isPublic: Boolean,
    val interval: ApiChallengeInterval,
    val kind: ApiChallengeKind,
    val goalType: ApiGoalType,
    val goalSteps: ApiSteps?,
    val goalMeasurement: ApiGoalMeasurement?,
    val partner: PartnerResponse?,
    val image: IdentifiedImageResponse?
)
