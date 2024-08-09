/*
 * Created by Kumpels and Friends on 2022-12-12
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.data.network.responses

import de.cleema.android.core.common.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class SurveyResponse(
    @Serializable(with = UUIDSerializer::class) val uuid: UUID,
    val title: String,
    val description: String,
    val finished: Boolean,
    val surveyUrl: String?,
    val evaluationUrl: String?,
)
