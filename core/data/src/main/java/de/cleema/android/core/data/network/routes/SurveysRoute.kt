/*
 * Created by Kumpels and Friends on 2022-12-12
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.data.network.routes

import de.cleema.android.core.data.network.responses.ApiResponse
import de.cleema.android.core.data.network.responses.SurveyResponse
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import java.util.*

internal interface SurveysRoute {
    @GET("api/surveys/")
    suspend fun getSurveys(): Result<ApiResponse<List<SurveyResponse>>>

    @PATCH("api/surveys/{id}/participate")
    suspend fun participateSurvey(@Path("id") id: UUID): Result<ApiResponse<SurveyResponse>>

    @PATCH("api/surveys/{id}/evaluate")
    suspend fun evaluateSurvey(@Path("id") id: UUID): Result<ApiResponse<SurveyResponse>>
}
