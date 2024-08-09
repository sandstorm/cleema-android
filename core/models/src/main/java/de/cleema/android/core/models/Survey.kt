/*
 * Created by Kumpels and Friends on 2022-12-12
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.models

import java.util.*

sealed interface SurveyState {
    data class Participation(val uri: String) : SurveyState
    data class Evaluation(val uri: String) : SurveyState
}

data class Survey(
    val id: UUID = UUID.randomUUID(),
    val title: String = "",
    val description: String = "",
    val state: SurveyState = SurveyState.Participation("")
)
