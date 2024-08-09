/*
 * Created by Kumpels and Friends on 2022-12-12
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.data.network

import de.cleema.android.core.data.network.responses.SurveyResponse
import java.util.*

interface SurveysDataSource {
    suspend fun getSurveys(): Result<List<SurveyResponse>>
    suspend fun evaluate(id: UUID): Result<SurveyResponse>
    suspend fun participate(id: UUID): Result<SurveyResponse>
}
